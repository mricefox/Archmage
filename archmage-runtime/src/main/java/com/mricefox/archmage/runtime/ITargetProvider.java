package com.mricefox.archmage.runtime;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/1/18
 */

public interface ITargetProvider {
    String group();

    Class<?> bindTargets(String path);
}