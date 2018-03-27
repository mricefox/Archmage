package com.mricefox.archmage.build.gradle.internal;

import com.android.build.gradle.BaseExtension;
import com.android.utils.FileUtils;
import com.google.common.io.ByteStreams;
import com.mricefox.archmage.build.gradle.extension.ArchmageSettingsExtension;

import org.gradle.api.Project;
import org.gradle.api.internal.plugins.DefaultConvention;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

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
        DefaultConvention convention = new HackGradleExtension(project.getGradle()).getConvention();
        URI uri = (URI) convention.getExtraProperties().get(ArchmageSettingsExtension.RUNTIME_JAR_URI_KEY);
        File classpath = null;
        try {
            classpath = downloadRuntimeJarAsNeeded(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classpath;
    }

    public static File getCacheDirectory() {
        File cacheDirectory =
                new File(FileUtils.join(System.getProperty("user.home"), ".archmage", "cache"));
        if (!cacheDirectory.isDirectory()) {
            cacheDirectory.mkdirs();
        }
        return cacheDirectory;
    }

    private static File downloadRuntimeJarAsNeeded(URI uri) throws IOException {
        String s = uri.toString();
        String name = s.substring(s.lastIndexOf('/') + 1);
        File cacheDirectory = getCacheDirectory();
        File target = new File(cacheDirectory, name);

//        logger.info("cacheDirectory:" + cacheDirectory);

        if (!target.isFile()) {
            logger.info("Download runtime jar from:" + uri + " to:" + target);
            URL source = uri.toURL();
            URLConnection connection = source.openConnection();

            try (OutputStream out = new FileOutputStream(target);
                 InputStream in = connection.getInputStream()) {
                ByteStreams.copy(in, out);
            }
        }
        return target;
    }

}
