package com.mricefox.archmage.build.gradle.internal;

import org.gradle.api.internal.plugins.DefaultConvention;
import org.gradle.api.invocation.Gradle;

import java.lang.reflect.Method;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description: hack gradle extension which is used to transfer info between settings and project
 * <p>Date:2018/1/8
 */

public class HackGradleExtension {
    private final Gradle gradle;

    public HackGradleExtension(Gradle gradle) {
        this.gradle = gradle;
    }

    public DefaultConvention getConvention() {
        try {
            Method method = gradle.getClass().getMethod("getExtensions");
            return (DefaultConvention) method.invoke(gradle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
