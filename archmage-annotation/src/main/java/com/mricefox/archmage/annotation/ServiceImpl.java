package com.mricefox.archmage.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:Mark service implementation
 * <p>Date:2018/1/24
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface ServiceImpl {
}
