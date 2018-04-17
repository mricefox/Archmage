package com.mricefox.archmage.runtime;

import android.app.Application;
import android.net.Uri;
import android.os.Bundle;

import java.util.List;

import static com.mricefox.archmage.runtime.Utils.checkNull;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/12/14
 */

public final class Archmage {
    private final static Logger LOGGER = Logger.getLogger(Archmage.class);
    private final static DependencyInjectionHook diHook = ArchmageAspectPlugins.inst().getDependencyInjectionHook();
    private final static LifecycleHook lifecycleHook = ArchmageAspectPlugins.inst().getLifecycleHook();

    private static boolean sInstalled = false;
    private static TargetUriParser uriParser = new DefaultTargetUriParser();

    private Archmage() {
    }

    public static void install(final Application application, boolean debug) {
        if (sInstalled) {
            LOGGER.warn("Archmage has already installed");
            return;
        }

        sInstalled = true;

        Logger.DEBUG = debug;

        List<String> classes = ActivatorRecord.getActivatorClasses();
        if (classes.isEmpty()) {
            LOGGER.info("No activator!");
        }

        for (String className : classes) {
            try {
                @SuppressWarnings("unchecked")
                Class<? extends ModuleActivator> c = (Class<? extends ModuleActivator>) Class.forName(className);
                ModuleActivator activator = c.newInstance();
                activator.attach(application);
                diHook.onActivatorAttachSuccess(className);
            } catch (Exception e) {
                if (!diHook.onActivatorAttachFailure(className, e)) {
                    throw new ArchmageException(e);
                }
            }
        }

        boot(application);
    }

    public static void urlParser(TargetUriParser targetUriParser) {
        uriParser = targetUriParser;
    }

    private static void boot(final Application application) {
        lifecycleHook.onStart();

        lifecycleHook.onDeclareDependencyStart();

        long t = System.nanoTime();
        LightBootTaskManager.inst().rawNodesSnapshot().applyToType(ArchmageModule.class, new ApplyCallback<ArchmageModule>() {
            @Override
            public void apply(ArchmageModule module) {
                module.declareBootDependency();
            }
        });
        lifecycleHook.onDeclareDependencyEnd(System.nanoTime() - t);

        lifecycleHook.onFlattenDependencyStart();
        t = System.nanoTime();
        LightBootTaskManager.inst().flattenDependency();
        HeavyBootTaskManager.inst().flattenDependency();
        lifecycleHook.onFlattenDependencyEnd(System.nanoTime() - t);

        final Bundle extra = new Bundle();
        final boolean isMainProcess = Utils.isMainProcess(application);

        LightBootTaskManager.inst().flattenNodesSnapshot().applyToAll(new ApplyCallback<LightBootTask>() {
            @Override
            public void apply(LightBootTask task) {
                if (!isMainProcess && !task.bootBesideMainProcess()) {
                    return;
                }

                lifecycleHook.onLightBootTaskStart(task.alias());
                long t = System.nanoTime();
                task.boot(application, extra);
                lifecycleHook.onLightBootTaskEnd(task.alias(), System.nanoTime() - t);
            }
        });

        lifecycleHook.onAllLightBootTaskDone();

        HeavyBootTaskManager.inst().flattenNodesSnapshot().applyToAll(new ApplyCallback<HeavyBootTask>() {
            @Override
            public void apply(HeavyBootTask task) {
                if (!isMainProcess && !task.bootBesideMainProcess()) {
                    return;
                }
                task.boot(application, extra);
            }
        });
    }

    /**
     * @throws ServiceNotFoundException
     */
    public static <Service extends IService> Service service(Class<Service> alias) {
        checkNull(alias, "Service alias");

        return ServiceManager.inst().findService(alias);
    }

    /**
     * Get service from callback
     */
    public static <Service extends IService> void service(Class<Service> alias, ServiceFindCallback<Service> callback) {
        checkNull(alias, "Service alias");
        checkNull(callback, "ServiceFindCallback");

        ServiceManager.inst().findService(alias, callback);
    }

    public static Transfer transfer(Uri uri) {
        return new Transfer(uri, uriParser);
    }

    public static void terminate(final Application application) {
        LightBootTaskManager.inst().flattenNodesSnapshot().applyToType(ArchmageModule.class, new ApplyCallback<ArchmageModule>() {
            @Override
            public void apply(ArchmageModule module) {
                module.appTerminate(application);
            }
        });
    }

}
