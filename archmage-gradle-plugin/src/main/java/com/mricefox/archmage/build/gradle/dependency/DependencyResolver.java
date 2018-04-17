package com.mricefox.archmage.build.gradle.dependency;

import com.android.utils.FileUtils;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.mricefox.archmage.build.gradle.internal.Environment;
import com.mricefox.archmage.build.gradle.internal.Logger;
import com.mricefox.archmage.build.gradle.internal.Utils;

import org.gradle.api.GradleException;
import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency;
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DependencyResolver {
    private static final String INTERMEDIATES_IN = "intermediates/archmage/in";
    private static final FileTime ZERO_TIME = FileTime.fromMillis(0);

    private final Logger logger = Logger.getLogger(DependencyResolver.class);
    private final Map<Dependency, List<String>> dependencyToPackages;
    private final Project project;
    private final File intermediateInDirectory;

    public DependencyResolver(Project project, Map<Dependency, List<String>> dependencyToPackages) {
        this.project = project;
        this.dependencyToPackages = dependencyToPackages;
        this.intermediateInDirectory = FileUtils.join(project.getBuildDir(), INTERMEDIATES_IN);
    }

    /**
     * 1. compile .class files from project dependency or download aar from remote dependency
     * 2. compile .class files to jar
     * 3. cache jar in user local directory
     */
    public File resolve() throws IOException {
        if (dependencyToPackages.isEmpty()) {
            return null;
        }
        if (intermediateInDirectory.isDirectory()) {
            FileUtils.deleteDirectoryContents(intermediateInDirectory);
        }

        File outputJar = FileUtils.join(intermediateInDirectory, "jar/classes.jar");

        File classesDirectory = FileUtils.join(intermediateInDirectory, "classes");
        project.mkdir(classesDirectory);

        for (Iterator<Map.Entry<Dependency, List<String>>> itr = dependencyToPackages.entrySet().iterator();
             itr.hasNext(); ) {
            Map.Entry<Dependency, List<String>> entry = itr.next();

            if (entry.getKey() instanceof DefaultProjectDependency) {
                //Unable to know the export packages of project dependency, so can not compare with
                //import packages
                resolveProjectDependency((DefaultProjectDependency) entry.getKey()
                        , entry.getValue(), classesDirectory);
            } else if (entry.getKey() instanceof DefaultExternalModuleDependency) {
                boolean success = resolveExternalDependency(
                        (DefaultExternalModuleDependency) entry.getKey(), entry.getValue()
                        , classesDirectory);
                if (!success) {
                    logger.error(project + " resolve external dependency:" + entry.getKey() + " failed");
                }
            } else {
                throw new GradleException("Archmage provided can only depend on aar or project");
            }
        }

        String md5 = md5ClassesDirectory(classesDirectory);

        compileJar(outputJar, classesDirectory);

        //jar generated during configure phase, execute cmd like 'gradlew clean assembleDebug' will
        //delete jar if cached in build directory, to avoid compile error(lake of provided jar), jar
        //cached outside build directory(~/.archmage/cache)
        return cacheOutputJar(outputJar, md5);
    }

    private File cacheOutputJar(File srcJar, String md5) throws IOException {
        File cacheDirectory = new File(Environment.getCacheDirectory(), md5);
        if (cacheDirectory.isDirectory()) {
            FileUtils.deleteDirectoryContents(cacheDirectory);
        }

        FileUtils.mkdirs(cacheDirectory);
        FileUtils.copyFileToDirectory(srcJar, cacheDirectory);

        return new File(cacheDirectory, srcJar.getName());
    }

    private void resolveProjectDependency(DefaultProjectDependency dependency
            , List<String> importPackages, File classesDirectory) {

        for (String pkgName : importPackages) {
            File pkgDirectory = dependency.getDependencyProject()
                    .file("src/main/java/" + Utils.packageToPath(pkgName));
            if (!pkgDirectory.exists()) {
                throw new GradleException("Archmage import package:" + pkgName + " not exist in "
                        + dependency.getDependencyProject());
            }

            compileJavac(
                    classesDirectory
                    , pkgDirectory
                    , Environment.getBootClasspath(project)
                    , Environment.getClasspath(project));
        }
    }

    private boolean resolveExternalDependency(DefaultExternalModuleDependency dependency
            , List<String> importPackages, File classesDirectory) throws IOException {
        List<MavenArtifactRepository> repositories = project.getRepositories().stream()
                .filter(repository -> repository instanceof MavenArtifactRepository)
                .map(repository -> (MavenArtifactRepository) repository)
//                .filter(repository -> !"jcenter.bintray.com".equalsIgnoreCase(repository.getUrl().getHost()))
                .collect(Collectors.toList());

        for (MavenArtifactRepository repository : repositories) {
            String url = concatAarUrl(repository.getUrl(), dependency);
            File directory = FileUtils.join(intermediateInDirectory, "download");
            directory.mkdirs();
            File aar = new File(directory, dependency.getName() + '-' + dependency.getVersion() + ".aar");

            try (OutputStream out = new FileOutputStream(aar);
                 InputStream in = new URL(url).openStream()) {
                ByteStreams.copy(in, out);
            } catch (IOException e) {
                logger.warn(project + " Download aar fail, url:" + url);
                continue;
            }
            logger.info(project + " Download aar success, url:" + url);

            unzipClassesFromAar(aar, classesDirectory);

            for (String pkgName : importPackages) {
                if (!FileUtils.join(classesDirectory
                        , Utils.packageToPath(pkgName)).exists()) {
                    throw new GradleException(project + " provided import package:" + pkgName + ", but does not exist in aar:" + aar);
                }
            }

            return true;
        }
        return false;
    }

    private static String concatAarUrl(URI uri, DefaultExternalModuleDependency dependency) {
        StringBuilder url = new StringBuilder(uri.toString())
                .append(dependency.getGroup().replace('.', '/'))
                .append('/')
                .append(dependency.getName())
                .append('/')
                .append(dependency.getVersion())
                .append('/')
                .append(dependency.getName())
                .append('-')
                .append(dependency.getVersion())
                .append(".aar");
        return url.toString();
    }

    private void unzipClassesFromAar(File aar, File targetDirectory) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(aar))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                try {
                    String name = entry.getName().replaceAll("\\\\", "/");
                    String prefix = "archmage/classes/";

                    if (name.startsWith(prefix)) {
                        String flattenName = name.substring(prefix.length());
                        if (entry.isDirectory()) {
                            new File(targetDirectory, flattenName).mkdirs();
                        } else {
                            OutputStream out = new FileOutputStream(new File(targetDirectory, flattenName));
                            ByteStreams.copy(zis, out);
                            out.close();
                        }
                    }
                } finally {
                    zis.closeEntry();
                }
            }
        }
    }

    private static String md5ClassesDirectory(File classesDirectory) {
        StringBuilder source = new StringBuilder();

        FileUtils.getAllFiles(classesDirectory)
                .filter(file -> file.getName().endsWith(".class"))
                .forEach(file -> {
                    try {
                        uniformFileTime(file);
                        String fileMd5 = Hashing.md5()
                                .hashBytes(com.google.common.io.Files.toByteArray(file))
                                .toString();
                        source.append(fileMd5);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        return Hashing.md5().hashString(source, Charsets.UTF_8).toString();
    }

    private static void uniformFileTime(File file) throws IOException {
        //same content <=> same md5, ignore time
        Path p = Paths.get(file.toString());
        Files.setAttribute(p, "creationTime", ZERO_TIME);
        Files.setAttribute(p, "lastModifiedTime", ZERO_TIME);
        Files.setAttribute(p, "lastAccessTime", ZERO_TIME);
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

    private void compileJar(File outputJar, File classesDirectory) {
        project.getAnt().invokeMethod("jar",
                new HashMap() {{
                    put("destfile", outputJar);
                    put("basedir", classesDirectory);
                }}
        );
    }
}