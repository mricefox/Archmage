package com.mricefox.archmage.runtime;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/1/30
 */

public abstract class DependencyInjectionHook {

    public void onActivatorAttachSuccess(String activatorClassName) {
    }

    /**
     * @return false - rethrow the exception; true - swallow it
     */
    public boolean onActivatorAttachFailure(String activatorClassName, Exception e) {
        return false;
    }

    public void onModuleRegistered(Class<? extends LightBootTaskAlias> alias) {
    }

    public void onServiceRegistered(Class<? extends IService> alias) {
    }

    public void onTargetRegistered(String group) {
    }
}
