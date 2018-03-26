package com.mricefox.archmage.build.gradle.module.properties;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/12/15
 */

public class ArchmageModuleProperties {
    private String packageName;
    private String activatorClassName;

    public ArchmageModuleProperties(String packageName, String activatorClassName) {
        this.packageName = packageName;
        this.activatorClassName = activatorClassName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getActivatorClassName() {
        return activatorClassName;
    }

    public void setActivatorClassName(String activatorClassName) {
        this.activatorClassName = activatorClassName;
    }

    @Override
    public String toString() {
        return "ArchmageModuleProperties{" +
                "packageName='" + packageName + '\'' +
                ", activatorClassName='" + activatorClassName + '\'' +
                '}';
    }
}
