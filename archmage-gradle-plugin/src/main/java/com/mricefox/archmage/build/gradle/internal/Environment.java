package com.mricefox.archmage.build.gradle.internal;

import com.android.build.gradle.BaseExtension;
import com.android.utils.FileUtils;
import com.google.common.io.ByteStreams;

import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

public class Environment {
    private static final Logger logger = Logger.getLogger(Environment.class);

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
            cacheDirectory.mkdirs();
        }
        return cacheDirectory;
    }

    private static File downloadBundleIfNotExists(Project project) {
        File target = new File(getCacheDirectory(), "archmage-runtime-1.0.0-bundle.jar");

        if (target.isFile()) {
            return target;
        }

        List<MavenArtifactRepository> repositories = project.getRootProject().getBuildscript().getRepositories().stream()
                .filter(repository -> repository instanceof MavenArtifactRepository)
                .map(repository -> (MavenArtifactRepository) repository)
//                .filter(repository -> !"jcenter.bintray.com".equalsIgnoreCase(repository.getUrl().getHost
                .collect(Collectors.toList());

        for (MavenArtifactRepository repository : repositories) {
            String url = repository.getUrl().toString()
                    + "com/mricefox/archmage/runtime/archmage-runtime/1.0.0/archmage-runtime-1.0.0-bundle.jar";
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
}
