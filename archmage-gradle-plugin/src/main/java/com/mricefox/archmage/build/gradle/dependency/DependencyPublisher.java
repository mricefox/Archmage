package com.mricefox.archmage.build.gradle.dependency;

import com.android.utils.FileUtils;
import com.google.common.io.ByteStreams;
import com.mricefox.archmage.build.gradle.extension.ArchmageExtension;
import com.mricefox.archmage.build.gradle.internal.Environment;
import com.mricefox.archmage.build.gradle.internal.Logger;
import com.mricefox.archmage.build.gradle.internal.Utils;
import com.mricefox.archmage.build.gradle.module.properties.ModulePropertiesProcessor;

import org.gradle.api.JavaVersion;
import org.gradle.api.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class DependencyPublisher {
    final Logger logger = Logger.getLogger(DependencyPublisher.class);
    public static final String INTERMEDIATES_OUT = "intermediates/archmage/out";

    private final Project project;
    private final ModulePropertiesProcessor propertiesGenerator;
    private final File intermediateOutDirectory;

    public DependencyPublisher(Project project, ModulePropertiesProcessor propertiesGenerator) {
        this.project = project;
        this.propertiesGenerator = propertiesGenerator;
        this.intermediateOutDirectory = FileUtils.join(project.getBuildDir(), INTERMEDIATES_OUT);
    }

    /**
     * 1.unzip original aar to 'unzipAar' directory
     * 2.javac export packages, generate .class to unzip directory, generate ArchmageModuleProperties.json
     * 3.backup original aar
     * 4.zip 'unzipAar' directory, override original aar
     *
     * @param originalAar
     */
    public void publish(File originalAar) throws IOException {
        ArchmageExtension extension = project.getExtensions().getByType(ArchmageExtension.class);
        List<String> exportPackages = extension.getExportPackages();
        String activatorClassName = ModulePropertiesProcessor.getProjectPackageName(project) + ".Activator_$$_";

        if (intermediateOutDirectory.isDirectory()) {
            FileUtils.deleteDirectoryContents(intermediateOutDirectory);
        }

        File unzipDirectory = FileUtils.join(intermediateOutDirectory, "unzipAar");
        unzipDirectory.mkdirs();

        unzipToDirectory(originalAar, unzipDirectory);

        File classesDirectory = FileUtils.join(unzipDirectory, "archmage", "classes");
        project.mkdir(classesDirectory);

        if (exportPackages != null) {
            for (String pkgName : exportPackages) {
                File pkgDirectory = project.file("src/main/java/" + Utils.packageToPath(pkgName));
                compileJavac(classesDirectory,
                        pkgDirectory,
                        Environment.getBootClasspath(project),
                        Environment.getClasspath(project));
            }
        }

        if (activatorExist(new File(unzipDirectory, "classes.jar"), activatorClassName)) {
            propertiesGenerator.generateProperties(activatorClassName, classesDirectory.getParentFile());
        } else {
            propertiesGenerator.generateProperties("", classesDirectory.getParentFile());
        }

        //backup original aar
        File bakDirectory = FileUtils.join(intermediateOutDirectory, "bak");
        project.mkdir(bakDirectory);

        FileUtils.copyFileToDirectory(originalAar, bakDirectory);

        zipDirectory(originalAar, unzipDirectory);
    }

    private static void unzipToDirectory(File zip, File directory) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zip))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                try {
                    String name = entry.getName();
                    if (entry.isDirectory()) {
                        new File(directory, name).mkdirs();
                    } else {
                        OutputStream out = new FileOutputStream(new File(directory, name));
                        ByteStreams.copy(zis, out);
                        out.close();
                    }
                } finally {
                    zis.closeEntry();
                }
            }
        }
    }

    private static void zipDirectory(File dest, File directory) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(dest))) {
            Files.walkFileTree(Paths.get(directory.toString()), new SimpleFileVisitor<Path>() {
                private String getEntryName(File child) {
                    String name = child.getAbsolutePath()
                            .substring(directory.getAbsolutePath().length() + 1);
                    if (child.isDirectory()) {
                        name += "/";
                    }
                    return name;
                }

                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                    String name = getEntryName(path.toFile());
                    ZipEntry entry = new ZipEntry(name);
                    zos.putNextEntry(entry);
                    try (FileInputStream fis = new FileInputStream(path.toFile())) {
                        ByteStreams.copy(fis, zos);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                    if (!path.toFile().equals(directory)) {
                        String name = getEntryName(path.toFile());
                        ZipEntry entry = new ZipEntry(name);
                        zos.putNextEntry(entry);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    private void compileJavac(File destDir, File srcDir, File bootClasspath, File classpath) {
        project.getAnt().invokeMethod("javac",
                new HashMap() {{
                    put("includeantruntime", false);
                    put("destdir", destDir);
                    put("srcdir", srcDir);
                    put("bootclasspath", bootClasspath);
                    put("classpath", classpath);
                    put("target", JavaVersion.VERSION_1_7);
                    put("source", JavaVersion.VERSION_1_7);
                }}
        );
    }

    private static boolean activatorExist(File classesJar, String activatorClassName) {
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(classesJar))) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                if (!entry.isDirectory()
                        && activatorClassName.replace('.', '/').concat(".class")
                        .equals(entry.getName().replaceAll("\\\\", "/"))) {
                    zipIn.closeEntry();
                    return true;
                }
                zipIn.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}