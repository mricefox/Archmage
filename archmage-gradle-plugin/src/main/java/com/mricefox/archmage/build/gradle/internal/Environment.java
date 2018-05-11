package com.mricefox.archmage.build.gradle.internal;

import com.android.build.gradle.BaseExtension;
import com.android.utils.FileUtils;
import com.google.common.io.ByteStreams;

import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.util.GUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class Environment {
    private static final Logger logger = Logger.getLogger(Environment.class);
    private static final String DEFAULT_BUNDLE_VERSION = "1.0.0";

    private Environment() {
    }

    public static File getBootClasspath(Project project) {
        BaseExtension extension = project.getExtensions().getByType(BaseExtension.class);
        File bootClasspath = extension.getBootClasspath().iterator().next();

        return bootClasspath;
    }

    public static File getClasspath(Project project) {
        return downloadBundleIfNotExists(project);
    }

    public static File getCacheDirectory() {
        File cacheDirectory =
                new File(FileUtils.join(System.getProperty("user.home"), ".archmage", "cache"));
        if (!cacheDirectory.isDirectory()) {
            FileUtils.mkdirs(cacheDirectory);
        }
        return cacheDirectory;
    }

    private static File downloadBundleIfNotExists(Project project) {
        String bundleVersion = getBundleVersion(project, DEFAULT_BUNDLE_VERSION);
        String bundleCoordinate =
                String.format("com/mricefox/archmage/runtime/archmage-runtime/%s/archmage-runtime-%s-bundle.jar",
                        bundleVersion, bundleVersion);
        File target = new File(getCacheDirectory(), bundleCoordinate);

        if (target.isFile()) {
            return target;
        }
        if (!target.getParentFile().isDirectory()) {
            FileUtils.mkdirs(target.getParentFile());
        }

        List<MavenArtifactRepository> repositories = project.getRootProject().getBuildscript().getRepositories()
                .stream()
                .filter(repository -> repository instanceof MavenArtifactRepository)
                .map(repository -> (MavenArtifactRepository) repository)
//                .filter(repository -> !"jcenter.bintray.com".equalsIgnoreCase(repository.getUrl().getHost
                .collect(Collectors.toList());

        for (MavenArtifactRepository repository : repositories) {
            String url = repository.getUrl().toString() + bundleCoordinate;
            try (OutputStream out = new FileOutputStream(target);
                 InputStream in = new URI(url).toURL().openStream()) {
                ByteStreams.copy(in, out);
                logger.warn(project + " download bundle success, url:" + url);
                break;
            } catch (URISyntaxException | IOException e) {
                logger.warn(project + " download bundle fail, url:" + url);
            }
        }
        return target;
    }

    private static String getBundleVersion(Project project, String defaultVersion) {
        File gradleProperties = new File(project.getRootProject().getRootDir(), "gradle.properties");

        if (gradleProperties.isFile()) {
            Properties properties = GUtil.loadProperties(gradleProperties);
            return properties.getProperty("archmage.runtime.version", defaultVersion);
        }
        return defaultVersion;
    }
}
