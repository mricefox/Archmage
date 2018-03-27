package com.mricefox.archmage.build.gradle;

import com.google.gson.Gson;
import com.mricefox.archmage.build.gradle.extension.ArchmageSettingsExtension;
import com.mricefox.archmage.build.gradle.internal.HackGradleExtension;
import com.mricefox.archmage.build.gradle.internal.Logger;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.internal.plugins.DefaultConvention;
import org.gradle.initialization.DefaultSettings;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import groovy.util.Node;
import groovy.util.XmlParser;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/1/8
 */

public class ArchmageSettingsPlugin implements Plugin<DefaultSettings> {
    public static final String EXT_KEY_INCLUDED_PROJECTS_NAME_JSON = "key_included_projects_name_json";
    public static final String EXT_KEY_ARTIFACT_VERSION_JSON = "key_artifact_ver_json";

    private final Logger logger = Logger.getLogger(ArchmageSettingsPlugin.class);
    private final ArchmageSettingsExtension extension = new ArchmageSettingsExtension();

    private DefaultSettings settings;

    @Override
    public void apply(DefaultSettings settings) {
        this.settings = settings;
    }

    public ArchmageSettingsExtension getExtension() {
        return extension;
    }

    public void applyConfigure() {
        logger.info("Apply configure, extension:" + extension);

        //add properties to gradle, so sub projects can read
        DefaultConvention convention = new HackGradleExtension(settings.getGradle()).getConvention();
        convention.getExtraProperties().set(ArchmageSettingsExtension.RUNTIME_JAR_URI_KEY, extension.getRuntimeJarUri());

        Set<String> subPaths = includeSubProjects();

        logger.info("subPaths:" + subPaths);

        convention.getExtraProperties().set(EXT_KEY_INCLUDED_PROJECTS_NAME_JSON, new Gson().toJson(subPaths));
        convention.getExtraProperties().set(EXT_KEY_ARTIFACT_VERSION_JSON, extension.getArtifactVersionText());
    }

    private Set<String> includeSubProjects() {
        Map<String, String> pathsToDir = extension.getSubProjectPathToDir();

        if (extension.isFullSourceMode()) {
            logger.info("Full source model enable");

            extension.getSubProjectPathToDir().entrySet().stream()
                    .forEach(entry -> {
                        settings.include(new String[]{entry.getKey()});
                        settings.project(entry.getKey()).setProjectDir(new File(settings.getRootDir(), entry.getValue()));
                    });
            return extension.getSubProjectPathToDir().keySet();
        }
        //only include project exists in manifest
        List<String> subProjectNames = getSubProjectNames(extension.getManifestText());
        logger.info("includeSubProjects, subProjectNames:" + subProjectNames);

        if (subProjectNames == null || subProjectNames.isEmpty()) {
            return Collections.emptySet();
        }

        File root = settings.getRootDir();

        HashSet<String> paths = new HashSet<>();

        subProjectNames.stream()
                .map(path -> ":" + path)
                .forEach(path ->
                        pathsToDir.forEach((p, dir) -> {
                            if (path.equals(p) || path.concat("-core").equals(p) || path.concat("-public").equals(p)) {
                                settings.include(new String[]{p});
                                settings.project(p).setProjectDir(new File(root, dir));

                                if (!paths.add(p)) {
                                    throw new GradleException("Duplicate path:" + p);
                                }
                            }
                        })
                );
        return new HashSet<>(paths);
    }

    @SuppressWarnings("unchecked")
    private List<String> getSubProjectNames(String manifestText) {
        try {
            Node root = new XmlParser().parseText(manifestText);

            if (!"manifest".equals(root.name())) {
                throw new IllegalArgumentException("Illegal manifest, root name:" + root.name());
            }

            List<Node> children = new LinkedList<>(root.children());

            return children.stream()
                    .filter(node -> "project".equals(node.name()))
                    .map(node -> node.attribute("name").toString())
                    .map(name -> name.contains("/") ? name.substring(name.indexOf('/') + 1) : name)
                    //exclude root project
                    .filter(name -> !name.equals(settings.getRootProject().getName()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
