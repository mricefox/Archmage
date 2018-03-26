package com.mricefox.archmage.build.gradle.artifact;

import java.util.Map;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/1/8
 */

public class ArtifactVersion {
    private Map<String, ArtifactCoordinate> artifacts;

    public Map<String, ArtifactCoordinate> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(Map<String, ArtifactCoordinate> artifacts) {
        this.artifacts = artifacts;
    }

    public static class ArtifactCoordinate {
        private String version;
        private String group;
        private String name;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "ArtifactCoordinate{" +
                    "version='" + version + '\'' +
                    ", group='" + group + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ArtifactVersion{" +
                "artifacts=" + artifacts +
                '}';
    }
}
