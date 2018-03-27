package com.mricefox.archmage.build.gradle.extension;

import java.util.List;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/12/15
 */

public class ArchmageProvidedExtension {
    private List<String> importPackages;

    public void setImportPackages(List<String> importPackages) {
        this.importPackages = importPackages;
    }

    public List<String> getImportPackages() {
        return importPackages;
    }
}
