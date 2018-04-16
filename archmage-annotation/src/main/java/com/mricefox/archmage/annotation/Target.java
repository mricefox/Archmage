package com.mricefox.archmage.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:Mark target
 * <p>Date:2018/4/7
 */
@Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target(ElementType.TYPE)
public @interface Target {
    String path();
}
