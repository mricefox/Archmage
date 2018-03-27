package com.mricefox.archmage.build.gradle.artifact;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mricefox.archmage.build.gradle.ArchmageSettingsPlugin;
import com.mricefox.archmage.build.gradle.internal.HackGradleExtension;
import com.mricefox.archmage.build.gradle.internal.Logger;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.internal.plugins.DefaultConvention;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import groovy.lang.Closure;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/1/9
 */

public class DependencyConverter {
    private static final Logger logger = Logger.getLogger(DependencyConverter.class);

    private DependencyConverter() {
    }

    @SuppressWarnings("unchecked")
    public static Dependency createDependency(Project project, String path, Closure configureClosure) {
        DefaultConvention convention = new HackGradleExtension(project.getGradle()).getConvention();

        String artifactVersionJson = (String) convention.getExtraProperties().get(ArchmageSettingsPlugin.EXT_KEY_ARTIFACT_VERSION_JSON);
        ArtifactVersion artifactVersion = new Gson().fromJson(artifactVersionJson, ArtifactVersion.class);

        String includedJson = (String) convention.getExtraProperties().get(ArchmageSettingsPlugin.EXT_KEY_INCLUDED_PROJECTS_NAME_JSON);
        Type setType =
                new TypeToken<Set<String>>() {
                }.getType();
        Set<String> includedProjectsName = new Gson().fromJson(includedJson, setType);

        if (includedProjectsName.contains(path)) {
            if (configureClosure != null) {
                return project.getDependencies().create(project.project(path), configureClosure);
            } else {
                return project.getDependencies().create(project.project(path));
            }
        } else {
            ArtifactVersion.ArtifactCoordinate artifactCoordinate = artifactVersion.getArtifacts().get(path.substring(1));

            if (artifactCoordinate == null) {
                throw new IllegalArgumentException(path.substring(1) + " artifact version not exist in artifact-version.json, artifactVersion:" + artifactVersion);
            }

            Map<String, String> artifactNotation = new LinkedHashMap() {{
                put("name", artifactCoordinate.getName());
                put("group", artifactCoordinate.getGroup());
                put("version", artifactCoordinate.getVersion());
            }};
            if (configureClosure != null) {
                return project.getDependencies().create(artifactNotation, configureClosure);
            } else {
                return project.getDependencies().create(artifactNotation);
            }
        }
    }
}
