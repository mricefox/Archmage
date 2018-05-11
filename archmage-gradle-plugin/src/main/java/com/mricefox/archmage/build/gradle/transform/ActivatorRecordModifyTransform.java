package com.mricefox.archmage.build.gradle.transform;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteStreams;
import com.mricefox.archmage.build.gradle.internal.Logger;
import com.mricefox.archmage.build.gradle.internal.Utils;

import org.gradle.api.GradleException;
import org.gradle.api.Project;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

import static com.android.build.api.transform.QualifiedContent.DefaultContentType.CLASSES;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/12/15
 */

public class ActivatorRecordModifyTransform extends Transform {
    static final String ACTIVATOR_RECORD_CLASS_NAME = "com.mricefox.archmage.runtime.ActivatorRecord";
    static final String MODULE_ACTIVATOR_CLASS_NAME = "com.mricefox.archmage.runtime.ModuleActivator";
    static final String ACTIVATOR_CLASS_SIMPLE_NAME = "Activator_$$_";

    private final Logger logger = Logger.getLogger(ActivatorRecordModifyTransform.class);
    private final Project project;
    private final Set<String> activators = new HashSet<>();

    //Jar contains ActivatorRecord.class
    private File activatorRecordJar;

    public ActivatorRecordModifyTransform(Project project) {
        this.project = project;
    }

    @Override
    public String getName() {
        return "activatorRecordModify";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return ImmutableSet.of(CLASSES);
    }

    @Override
    public Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }


    @Override
    public void transform(TransformInvocation invocation) throws TransformException, InterruptedException, IOException {
        super.transform(invocation);

        if (!invocation.isIncremental()) {
            invocation.getOutputProvider().deleteAll();
        }

        List<Collection<JarInput>> jars = new ArrayList<>();
        List<Collection<DirectoryInput>> dirs = new ArrayList<>();

        invocation.getInputs().forEach(input -> {
            jars.add(input.getJarInputs());
            dirs.add(input.getDirectoryInputs());
        });

        //find Jar contains ActivatorRecord.class
        jars.stream().flatMap(Collection::stream).forEach(jarInput -> {
            //ActivatorRecord.class exist only in archmage-runtime.jar, so just search in jar
            collectActivatorAndActivatorRecordInSubModule(jarInput.getFile());
        });

        if (activatorRecordJar == null) {
            throw new AssertionError("No ActivatorRecord.class, probably not contains archmage-runtime sdk");
        }

        dirs.stream().flatMap(Collection::stream).forEach(directoryInput -> {
            //application module can also contains activator
            try {
                collectActivatorInAppModule(directoryInput.getFile());

                File outputDirectory = invocation.getOutputProvider().getContentLocation(
                        directoryInput.getFile().getName(),
                        directoryInput.getContentTypes(),
                        directoryInput.getScopes(),
                        Format.DIRECTORY);
                FileUtils.copyDirectory(directoryInput.getFile(), outputDirectory);
            } catch (IOException | NotFoundException e) {
                e.printStackTrace();
            }
        });

        if (activators.isEmpty()) {
            logger.warn("No activator at all!");
        }

        jars.stream().flatMap(Collection::stream).forEach(jarInput -> {
            File input = jarInput.getFile();
            File output = invocation.getOutputProvider().getContentLocation(
                    jarInput.getName(),
                    jarInput.getContentTypes(),
                    jarInput.getScopes(),
                    Format.JAR);

            FileUtils.mkdirs(output.getParentFile());

            try {
                if (input.equals(activatorRecordJar)) {
                    copyActivatorRecordJar(input, output);
                } else {
                    FileUtils.copyFile(input, output);
                }
            } catch (IOException | CannotCompileException | NotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    private void collectActivatorAndActivatorRecordInSubModule(File jar) {
        try (JarFile inputJar = new JarFile(jar)) {
            for (Enumeration<JarEntry> e = inputJar.entries(); e.hasMoreElements(); ) {
                JarEntry entry = e.nextElement();

                if (entry.isDirectory()) {
                    continue;
                }
                if (ACTIVATOR_RECORD_CLASS_NAME.replace('.', '/').concat(".class").equals(entry.getName())) {
                    logger.info("ActivatorRecord.class found in jar:" + jar);
                    if (activatorRecordJar != null) {
                        throw new AssertionError("Duplicate ActivatorRecord.class");
                    }
                    activatorRecordJar = jar;
                } else if (entry.getName().endsWith(ACTIVATOR_CLASS_SIMPLE_NAME + ".class")) {
                    try (InputStream is = inputJar.getInputStream(entry)) {
                        String activatorClassName = getActivatorClassNameFromBytecode(is);

                        if (activatorClassName != null) {
                            logger.info("Activator:" + activatorClassName + " found in jar:" + jar);

                            if (!activators.add(activatorClassName)) {
                                throw new GradleException("Duplicate activator:" + activatorClassName + " in jar:" +
                                        jar);
                            }
                        }
                    } catch (NotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getActivatorClassNameFromBytecode(InputStream classFile) throws NotFoundException, IOException {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.makeClass(classFile);

        //todo check super class
        if (ACTIVATOR_CLASS_SIMPLE_NAME.equals(ctClass.getSimpleName()) &&
//                MODULE_ACTIVATOR_CLASS_NAME.equals(ctClass.getSuperclass().getName()) &&
                ctClass.getModifiers() == (Modifier.PUBLIC | Modifier.FINAL)) {
            return ctClass.getName();
        }
        return null;
    }

    private void collectActivatorInAppModule(File directory) throws IOException, NotFoundException {
        String qualifiedName = Utils.getProjectPackageName(project) + "." + ACTIVATOR_CLASS_SIMPLE_NAME;
        File file = new File(directory, Utils.packageToPath(qualifiedName) + ".class");

        if (file.isFile()) {
            FileInputStream is = new FileInputStream(file);
            String name = getActivatorClassNameFromBytecode(is);
            is.close();

            if (qualifiedName.equals(name)) {
                if (!activators.add(qualifiedName)) {
                    throw new AssertionError("Activator in application module has same qualified name with sub " +
                            "module's");
                } else {
                    logger.info("Activator exists in application module, class file:" + file);
                }
            } else {
                logger.info("Activator.class with path:" + file + " has different name in bytecode:" + name);
            }
        }
    }

    private void copyActivatorRecordJar(File input, File output) throws IOException, NotFoundException,
            CannotCompileException {
        if (activators.isEmpty()) {
            //no need to do injection
            FileUtils.copyFile(input, output);
        }

        try (JarInputStream in = new JarInputStream(new FileInputStream(input));
             JarOutputStream out = new JarOutputStream(new FileOutputStream(output))) {
            for (JarEntry entry = in.getNextJarEntry(); entry != null; entry = in.getNextJarEntry()) {
                String entryName = entry.getName();
                JarEntry newEntry = new JarEntry(entry);

                out.putNextEntry(newEntry);
                if (ACTIVATOR_RECORD_CLASS_NAME.replace('.', '/').concat(".class").equals(entryName)) {
                    injectActivators(in, out);
                } else if (!entry.isDirectory()) {
                    ByteStreams.copy(in, out);
                }
            }
        }
    }

    private void injectActivators(InputStream in, OutputStream out) throws IOException, NotFoundException,
            CannotCompileException {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ByteArrayClassPath(ACTIVATOR_RECORD_CLASS_NAME, ByteStreams.toByteArray(in)));

        CtClass ctClass = classPool.get(ACTIVATOR_RECORD_CLASS_NAME);
        ctClass.defrost();

        CtMethod ctMethod = ctClass.getDeclaredMethod("getActivatorClasses");
        StringBuilder methodBody = new StringBuilder();

        methodBody.append("{")
                .append("if(sActivatorClasses.isEmpty())")
                .append("{");
        activators.forEach(name -> {
            methodBody
                    .append("sActivatorClasses.add(\"")
                    .append(name)
                    .append("\");");
        });
        methodBody.append("}")
                .append("return sActivatorClasses;")
                .append("}");

        ctMethod.setBody(methodBody.toString());
        ctClass.toBytecode(new DataOutputStream(out));
    }
}
