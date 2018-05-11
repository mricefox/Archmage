package com.mricefox.archmage.build.gradle.internal;

import org.gradle.api.GradleException;
import org.gradle.api.Project;

import java.io.File;
import java.util.HashMap;

import groovy.lang.Closure;
import groovy.util.Node;
import groovy.util.XmlParser;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/12/20
 */

public class Utils {
    private Utils() {
    }

    public static String packageToPath(String pkgName) {
        if (pkgName == null || pkgName.trim().length() == 0) {
            throw new GradleException("Illegal package name:" + pkgName);
        }
        return pkgName.replace('.', '/');
    }

    public static void configureClosureDelegate(Object delegate, Closure closure) {
        closure.setDelegate(delegate);
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();
    }

    public static String getProjectPackageName(Project project) {
        try {
            Node manifest = new XmlParser().parse(project.file("src/main/AndroidManifest.xml"));
            return (String) manifest.attribute("package");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
