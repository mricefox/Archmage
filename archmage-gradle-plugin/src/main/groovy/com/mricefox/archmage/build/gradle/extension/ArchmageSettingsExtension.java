package com.mricefox.archmage.build.gradle.extension;

import java.net.URI;
import java.util.Map;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/1/8
 */

public class ArchmageSettingsExtension {
    public static final String RUNTIME_JAR_URI_KEY = "runtime_jar_uri";

    private String manifestText;
    private String artifactVersionText;
    private Map<String, String> subProjectPathToDir;
    private boolean fullSourceModel;
    private URI runtimeJarUri;

    public String getManifestText() {
        return manifestText;
    }

    public void setManifestText(String manifestText) {
        this.manifestText = manifestText;
    }

    public String getArtifactVersionText() {
        return artifactVersionText;
    }

    public void setArtifactVersionText(String artifactVersionText) {
        this.artifactVersionText = artifactVersionText;
    }

    public Map<String, String> getSubProjectPathToDir() {
        return subProjectPathToDir;
    }

    public void setSubProjectPathToDir(Map<String, String> subProjectPathToDir) {
        this.subProjectPathToDir = subProjectPathToDir;
    }

    public boolean isFullSourceModel() {
        return fullSourceModel;
    }

    public void setFullSourceModel(boolean fullSourceModel) {
        this.fullSourceModel = fullSourceModel;
    }

    public URI getRuntimeJarUri() {
        return runtimeJarUri;
    }

    public void setRuntimeJarUri(URI runtimeJarUri) {
        this.runtimeJarUri = runtimeJarUri;
    }

    @Override
    public String toString() {
        return "ArchmageSettingsExtension{" +
                "manifestText='" + manifestText + '\'' +
                ", artifactVersionText='" + artifactVersionText + '\'' +
                ", subProjectPathToDir=" + subProjectPathToDir +
                ", fullSourceModel=" + fullSourceModel +
                ", runtimeJarUri=" + runtimeJarUri +
                '}';
    }
}
