package com.mricefox.archmage.build.gradle;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import com.android.build.gradle.BaseExtension;
import com.android.build.gradle.LibraryPlugin;
import com.android.utils.FileUtils;
import com.mricefox.archmage.build.gradle.dependency.DependencyPublisher;
import com.mricefox.archmage.build.gradle.dependency.DependencyResolver;
import com.mricefox.archmage.build.gradle.extension.ArchmageExtension;
import com.mricefox.archmage.build.gradle.internal.Environment;
import com.mricefox.archmage.build.gradle.internal.Logger;
import com.mricefox.archmage.build.gradle.module.properties.ActivatorRecordAlterTransform;
import com.mricefox.archmage.build.gradle.module.properties.ModulePropertiesProcessor;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.DependencyResolutionListener;
import org.gradle.api.artifacts.ResolvableDependencies;
import org.gradle.api.plugins.PluginContainer;

import java.io.File;
import java.io.IOException;

public class ArchmageBuildPlugin implements Plugin<Project> {
    private static final Logger LOGGER = Logger.getLogger(ArchmageBuildPlugin.class);
    private ModulePropertiesProcessor propertiesProcessor;

    @Override
    public void apply(Project project) {
        PluginContainer pluginContainer = project.getPlugins();

        if (!pluginContainer.hasPlugin(AppPlugin.class) && !pluginContainer.hasPlugin(LibraryPlugin.class)) {
            throw new GradleException("Archmage plugin can only apply in android project");
        }

        project.getExtensions().create("archmage", ArchmageExtension.class, project);

        propertiesProcessor = new ModulePropertiesProcessor(project);

        //configure annotation processor
        project.getDependencies().add("annotationProcessor"
                , "com.mricefox.archmage.processor:archmage-anno-processor:1.0.1");
//        project.getDependencies().add("annotationProcessor", project.project(":archmage-anno-processor"));

        BaseExtension extension = project.getExtensions().getByType(BaseExtension.class);
        extension.getDefaultConfig().getJavaCompileOptions().getAnnotationProcessorOptions()
                .getArguments().put("archmage_module_packageName", ModulePropertiesProcessor.getProjectPackageName(project));


        if (pluginContainer.hasPlugin(AppPlugin.class)) {
            hookApplicationBuild(project);
        } else if (pluginContainer.hasPlugin(LibraryPlugin.class)) {
            hookLibraryBuild(project);
        }
    }

    private void hookLibraryBuild(Project project) {
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
                            project.getDependencies().add("provided", project.files(outputJar));
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
                File outputAar = bundleReleaseTask.getOutputs().getFiles().getSingleFile();
                bundleReleaseTask.doLast(task -> {
                    try {
                        new DependencyPublisher(project, propertiesProcessor).publish(outputAar);
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
        project.getExtensions().getByType(AppExtension.class)
                .registerTransform(new ActivatorRecordAlterTransform(project, propertiesProcessor));
    }
}