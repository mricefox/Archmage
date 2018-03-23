package com.mricefox.archmage.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/1/24
 */
@Retention(RetentionPolicy.CLASS)
public @interface TargetMapping {
    String path();

    Class<?> target();
}
