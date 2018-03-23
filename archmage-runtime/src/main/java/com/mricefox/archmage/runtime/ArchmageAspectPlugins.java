package com.mricefox.archmage.runtime;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/1/23
 */

public class ArchmageAspectPlugins {
    private DependencyLookupHook dependencyLookupHook;
    private DependencyInjectionHook dependencyInjectionHook;
    private LifecycleHook lifecycleHook;

    private ArchmageAspectPlugins() {
    }

    private static final class InstanceHolder {
        private static final ArchmageAspectPlugins INSTANCE = new ArchmageAspectPlugins();
    }

    public static ArchmageAspectPlugins inst() {
        return InstanceHolder.INSTANCE;
    }

    private static class DependencyLookupHookDefault extends DependencyLookupHook {
        private static DependencyLookupHookDefault INSTANCE = new DependencyLookupHookDefault();

        private DependencyLookupHookDefault() {
        }

        private static DependencyLookupHookDefault inst() {
            return INSTANCE;
        }
    }

    private static class DependencyInjectionHookDefault extends DependencyInjectionHook {
        private static DependencyInjectionHookDefault INSTANCE = new DependencyInjectionHookDefault();

        private DependencyInjectionHookDefault() {
        }

        private static DependencyInjectionHookDefault inst() {
            return INSTANCE;
        }
    }

    private static class LifecycleHookDefault extends LifecycleHook {
        private static LifecycleHookDefault INSTANCE = new LifecycleHookDefault();

        private LifecycleHookDefault() {
        }

        private static LifecycleHookDefault inst() {
            return INSTANCE;
        }
    }

    public void registerDependencyLookupHook(DependencyLookupHook impl) {
        dependencyLookupHook = impl;
    }

    public DependencyLookupHook getDependencyLookupHook() {
        if (dependencyLookupHook == null) {
            dependencyLookupHook = DependencyLookupHookDefault.inst();
        }
        return dependencyLookupHook;
    }

    public void registerDependencyInjectionHook(DependencyInjectionHook impl) {
        dependencyInjectionHook = impl;
    }

    public DependencyInjectionHook getDependencyInjectionHook() {
        if (dependencyInjectionHook == null) {
            dependencyInjectionHook = DependencyInjectionHookDefault.inst();
        }
        return dependencyInjectionHook;
    }

    public void registerLifecycleHook(LifecycleHook impl) {
        lifecycleHook = impl;
    }

    public LifecycleHook getLifecycleHook() {
        if (lifecycleHook == null) {
            lifecycleHook = LifecycleHookDefault.inst();
        }
        return lifecycleHook;
    }
}
