package com.mricefox.archmage.build.gradle.extension;

import com.mricefox.archmage.build.gradle.internal.Utils;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import groovy.lang.Closure;

public class ArchmageExtension {
    private final Project project;
    private final Map<Dependency, List<String>> dependencyToPackages = new HashMap<>();

    private List<String> exportPackages;

    public ArchmageExtension(Project project) {
        this.project = project;
    }

    public void provided(Object dependencyNotation, Closure configureClosure) {
        Dependency dependency = project.getDependencies().create(dependencyNotation);
        ArchmageProvidedExtension ext = new ArchmageProvidedExtension();

        Utils.configureClosureDelegate(ext, configureClosure);
        if (dependencyToPackages.containsKey(dependency)) {
            throw new GradleException("Archmage provided with duplicate dependency:" + dependency);
        }
        dependencyToPackages.put(dependency, ext.getImportPackages());
    }

    public void setExportPackages(List<String> exportPackages) {
        for (String pkgName : exportPackages) {
            File pkgDirectory = project.file("src/main/java/" + Utils.packageToPath(pkgName));
            if (!pkgDirectory.exists()) {
                throw new GradleException("Archmage export package:" + pkgName + " not exist in "
                        + project);
            }
        }
        this.exportPackages = exportPackages;
    }

    public List<String> getExportPackages() {
        return exportPackages;
    }

    public Map<Dependency, List<String>> getDependencyToPackages() {
        return dependencyToPackages;
    }
}