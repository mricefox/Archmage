package com.mricefox.archmage.runtime;

import android.app.Application;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:Base class of activator, provide api to register boot node, targets and services
 * <p>Date:2017/12/19
 */

public abstract class ModuleActivator {
    private DependencyInjectionHook diHook = ArchmageAspectPlugins.inst().getDependencyInjectionHook();

    protected abstract void attach(Application application);

    protected final <Service extends IService> void registerService(Class<Service> alias, Service service) {
        ServiceManager.inst().registerService(alias, service);
        diHook.onServiceRegistered(alias);
    }

    protected final void registerModule(ArchmageModule module) {
        LightBootTaskManager.inst().addDependencyNode(module.alias(), module);
        diHook.onModuleRegistered(module.alias());
    }

    protected final void registerTargetProvider(ITargetProvider provider) {
        TargetProviderManager.inst().registerTargetProvider(provider.group(), provider);
        diHook.onTargetRegistered(provider.group());
    }
}
