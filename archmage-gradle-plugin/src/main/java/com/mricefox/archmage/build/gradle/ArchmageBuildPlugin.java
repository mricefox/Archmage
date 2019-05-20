package com.mricefox.archmage.build.gradle;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import com.android.build.gradle.LibraryExtension;
import com.android.build.gradle.LibraryPlugin;
import com.android.utils.FileUtils;
import com.mricefox.archmage.build.gradle.dependency.DependencyPublisher;
import com.mricefox.archmage.build.gradle.dependency.DependencyResolver;
import com.mricefox.archmage.build.gradle.extension.ArchmageExtension;
import com.mricefox.archmage.build.gradle.internal.Environment;
import com.mricefox.archmage.build.gradle.internal.Logger;
import com.mricefox.archmage.build.gradle.internal.Utils;
import com.mricefox.archmage.build.gradle.transform.ActivatorRecordModifyTransform;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.DependencyResolutionListener;
import org.gradle.api.artifacts.ResolvableDependencies;
import org.gradle.api.plugins.PluginContainer;
import org.jetbrains.kotlin.gradle.internal.Kapt3GradleSubplugin;
import org.jetbrains.kotlin.gradle.plugin.KaptAnnotationProcessorOptions;
import org.jetbrains.kotlin.gradle.plugin.KaptExtension;

import java.io.File;
import java.io.IOException;

import groovy.lang.Closure;


public class ArchmageBuildPlugin implements Plugin<Project> {
    private static final Logger LOGGER = Logger.getLogger(ArchmageBuildPlugin.class);

    @Override
    public void apply(Project project) {
        PluginContainer pluginContainer = project.getPlugins();

        if (!pluginContainer.hasPlugin(AppPlugin.class) && !pluginContainer.hasPlugin(LibraryPlugin.class)) {
            throw new GradleException("Archmage plugin can only apply in android project");
        }

        project.getExtensions().create("archmage", ArchmageExtension.class, project);

        /*
        * Since android-gradle-plugin 3.0 change the return type of method BaseExtension.getDefaultConfig from
        * ProductFlavor to DefaultConfig, set argument to annotation processor as below will conflict while
        * ArchmageBuildPlugin is build with android-gradle-plugin 2.2(for low version compat) but apply in project
        * which use android-gradle-plugin 3.0
        *
        * BaseExtension extension = project.getExtensions().getByType(BaseExtension.class);
        * extension.getDefaultConfig().getJavaCompileOptions()
        *          .getAnnotationProcessorOptions()
        *          .getArguments()
        *          .put("archmage_module_packageName", ModulePropertiesProcessor.getProjectPackageName(project));
        *
        * Here solution is set javac option -A as below
        *
        * variant.getJavaCompile().getOptions().getCompilerArgs()
        *               .add("-Aarchmage_module_packageName=" + Utils.getProjectPackageName(project));
        * */

        if (pluginContainer.hasPlugin(AppPlugin.class)) {
            hookApplicationBuild(project);
        } else if (pluginContainer.hasPlugin(LibraryPlugin.class)) {
            hookLibraryBuild(project);
        }
    }

    private void hookLibraryBuild(Project project) {
        configureAnnoProcessor(project, false);

        project.getGradle().addListener(new DependencyResolutionListener() {

            @Override
            public void beforeResolve(ResolvableDependencies resolvableDependencies) {
                //add 'archmage provided' dependency to project
                ArchmageExtension extension = project.getExtensions().getByType(ArchmageExtension.class);

                if (!extension.getDependencyToPackages().isEmpty()) {
                    DependencyResolver dependency =
                            new DependencyResolver(project, extension.getDependencyToPackages());

                    try {
                        File outputJar = dependency.resolve();

                        if (outputJar != null) {
                            boolean plugin3Enable = project.hasProperty("buildPlugin3Enable")
                                    && Boolean.valueOf(String.valueOf(project.getProperties().get("buildPlugin3Enable")));
                            project.getDependencies().add(plugin3Enable ? "compileOnly" : "provided", project.files(outputJar));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                project.getGradle().removeListener(this);
            }

            @Override
            public void afterResolve(ResolvableDependencies resolvableDependencies) {
                //unreachable
            }
        });

        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                //add action after bundleRelease task to bundle export packages
                Task bundleReleaseTask = project.getTasks().findByName("bundleRelease");
                if (bundleReleaseTask == null) {
                    bundleReleaseTask = project.getTasks().findByName("bundleReleaseAar");
                }
                if (bundleReleaseTask == null) {
                    throw new GradleException("Neither bundleRelease nor bundleReleaseAar was found");
                }
                File outputAar = bundleReleaseTask.getOutputs().getFiles().getSingleFile();
                bundleReleaseTask.doLast(task -> {
                    try {
                        new DependencyPublisher(project).publish(outputAar);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                project.task("cleanArchmageCache").doLast(task -> {
                    try {
                        if (Environment.getCacheDirectory().isDirectory()) {
                            FileUtils.deleteDirectoryContents(Environment.getCacheDirectory());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).setGroup("archmage");

                //No action actually, dependency resolved during configuration phase
                project.task("resolveArchmageDependency").setGroup("archmage");
            }
        });
    }

    private void hookApplicationBuild(Project project) {
        configureAnnoProcessor(project, true);

        project.getExtensions().getByType(AppExtension.class)
                .registerTransform(new ActivatorRecordModifyTransform(project));
    }

    private void configureAnnoProcessor(Project project, boolean app) {
        boolean isKotlin = project.getPlugins().hasPlugin(Kapt3GradleSubplugin.class);

        project.getDependencies().add(isKotlin ? "kapt" : "annotationProcessor"
                , "com.mricefox.archmage.processor:archmage-anno-processor:1.0.2");

        if (isKotlin) {
            project.getExtensions().getByType(KaptExtension.class).arguments(new Closure(null) {

                void doCall() {
                    KaptAnnotationProcessorOptions options = (KaptAnnotationProcessorOptions) getDelegate();
                    options.arg("archmage_module_packageName", Utils.getProjectPackageName(project));
                }
            });
        } else {
            if (app) {
                project.getExtensions().getByType(AppExtension.class).getApplicationVariants().all(variant ->
                        variant.getJavaCompile().getOptions().getCompilerArgs()
                                .add("-Aarchmage_module_packageName=" + Utils.getProjectPackageName(project)));
            } else {
                project.getExtensions().getByType(LibraryExtension.class).getLibraryVariants().all(variant ->
                        variant.getJavaCompile().getOptions().getCompilerArgs()
                                .add("-Aarchmage_module_packageName=" + Utils.getProjectPackageName(project)));
            }
        }
    }
}