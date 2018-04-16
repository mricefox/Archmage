package com.mricefox.archmage.build.gradle.module.properties;

import com.android.build.api.transform.Format;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteStreams;
import com.mricefox.archmage.build.gradle.internal.Logger;
import com.mricefox.archmage.build.gradle.internal.Utils;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ResolvedArtifact;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import static com.android.build.api.transform.QualifiedContent.DefaultContentType.CLASSES;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/12/15
 */

public class ActivatorRecordAlterTransform extends Transform {
    static final String ACTIVATOR_RECORD_CLASS = "com.mricefox.archmage.runtime.ActivatorRecord";

    private final Logger logger = Logger.getLogger(ActivatorRecordAlterTransform.class);
    private final Project project;
    private final ModulePropertiesProcessor propertiesProcessor;
    private String appActivatorClass = null;

    public ActivatorRecordAlterTransform(Project project, ModulePropertiesProcessor propertiesProcessor) {
        this.project = project;
        this.propertiesProcessor = propertiesProcessor;
    }

    @Override
    public String getName() {
        return "alterActivatorRecord";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return ImmutableSet.of(CLASSES);
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return EnumSet.of(
                QualifiedContent.Scope.PROJECT,
                QualifiedContent.Scope.PROJECT_LOCAL_DEPS,
                QualifiedContent.Scope.SUB_PROJECTS,
                QualifiedContent.Scope.SUB_PROJECTS_LOCAL_DEPS,
                QualifiedContent.Scope.EXTERNAL_LIBRARIES);
    }

    @Override
    public boolean isIncremental() {
        return false;
    }


    @Override
    public void transform(TransformInvocation transformInvocation)
            throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);

        if (!transformInvocation.isIncremental()) {
            transformInvocation.getOutputProvider().deleteAll();
        }

        transformInvocation.getInputs().forEach(transformInput -> {
            transformInput.getDirectoryInputs().forEach(directoryInput -> {
                logger.info("Input directory:" + directoryInput.getFile());

                String activatorClassName = ModulePropertiesProcessor.getProjectPackageName(project) + ".Activator_$$_";
                File activatorClassFile = new File(directoryInput.getFile(), Utils.packageToPath(activatorClassName) + ".class");
                if (activatorClassFile.isFile()) {
                    logger.info("Activator exists in application, class file:" + activatorClassFile);
                    appActivatorClass = activatorClassName;
                }

                File outputDir = transformInvocation.getOutputProvider().getContentLocation(
                        directoryInput.getFile().getName(),
                        directoryInput.getContentTypes(),
                        directoryInput.getScopes(),
                        Format.DIRECTORY);
                logger.info("Output directory:" + outputDir);

                try {
                    org.apache.commons.io.FileUtils.copyDirectory(directoryInput.getFile(), outputDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });

        transformInvocation.getInputs().forEach(transformInput -> {
                    //traverse jar, find and modify ActivatorRecord.class
                    List<String> outputJars = new ArrayList<>();
                    transformInput.getJarInputs().forEach(jarInput -> {
                                File outputJar = transformInvocation.getOutputProvider().getContentLocation(
                                        jarInput.getName(),
                                        jarInput.getContentTypes(),
                                        jarInput.getScopes(),
                                        Format.JAR);

                                if (outputJars.contains(outputJar.toString())) {
                                    throw new GradleException("Duplicate output jar:" + outputJar);
                                }
                                outputJars.add(outputJar.toString());

                                try {
                                    alterAndCopyJar(jarInput.getFile(), outputJar);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                    );
                }
        );
    }

    private void alterAndCopyJar(File inputJar, File outputJar) throws IOException {
        outputJar.getParentFile().mkdirs();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(inputJar));
             ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputJar))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                try {
                    String name = entry.getName();

                    if (ACTIVATOR_RECORD_CLASS
                            .replace('.', '/').concat(".class")
                            .equals(name)) {
                        logger.info("Find record class in jar:" + inputJar + " output:" + outputJar);

                        ZipEntry newEntry = new ZipEntry(name);
                        newEntry.setTime(entry.getTime());

                        zos.putNextEntry(newEntry);
                        regenerateAndExportClass(zis, new DataOutputStream(zos));
                    } else {
                        ZipEntry newEntry = new ZipEntry(name);
                        newEntry.setTime(entry.getTime());

                        zos.putNextEntry(newEntry);

                        if (!entry.isDirectory()) {
                            ByteStreams.copy(zis, zos);
                        }
                    }
                } catch (CannotCompileException | NotFoundException e) {
                    e.printStackTrace();
                } finally {
                    zis.closeEntry();
                }
            }
        }
    }

    private void regenerateAndExportClass(InputStream in, DataOutputStream out)
            throws IOException, NotFoundException, CannotCompileException {
        List<ArchmageModuleProperties> properties = findArchmageModuleProperties();
        if (properties.isEmpty()) {
            logger.info("No properties found");
        } else {
            properties.forEach(p -> logger.info("Property:" + p));
        }

        List<String> activatorClassNames = properties.stream()
                .map(ArchmageModuleProperties::getActivatorClassName)
                .filter(name -> name != null && !name.trim().isEmpty())
                .collect(Collectors.toList());
        //append activator in app
        if (appActivatorClass != null) {
            activatorClassNames.add(appActivatorClass);
        }

        if (activatorClassNames.isEmpty()) {
            ByteStreams.copy(in, out);
            return;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ByteStreams.copy(in, bos);

        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ByteArrayClassPath(ACTIVATOR_RECORD_CLASS, bos.toByteArray()));

        CtClass ctClass = classPool.get(ACTIVATOR_RECORD_CLASS);
        ctClass.defrost();

        CtMethod ctMethod = ctClass.getDeclaredMethod("getActivatorClasses");

        StringBuilder methodBody = new StringBuilder();
        methodBody.append("{")
                .append("if(sActivatorClasses.isEmpty())")
                .append("{");
        for (String name : activatorClassNames) {
            logger.info("Activator class name:" + name);
            methodBody
                    .append("sActivatorClasses.add(\"")
                    .append(name)
                    .append("\");");
        }
        methodBody.append("}")
                .append("return sActivatorClasses;")
                .append("}");

        ctMethod.setBody(methodBody.toString());
        ctClass.toBytecode(out);
    }

    /**
     * find properties in dependency aar
     */
    private List<ArchmageModuleProperties> findArchmageModuleProperties() {
        return project.getConfigurations().getByName("compile")
                .getResolvedConfiguration().getResolvedArtifacts()
                .stream()
                .filter(resolvedArtifact -> "aar".equalsIgnoreCase(resolvedArtifact.getExtension()))
                .map(ResolvedArtifact::getFile)
                .filter(File::exists)
                .map(propertiesProcessor::findArchmageModuleProperties)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
