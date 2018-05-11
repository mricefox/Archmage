package com.mricefox.archmage.sample.app;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import com.mricefox.archmage.runtime.Archmage;
import com.mricefox.archmage.runtime.ArchmageAspectPlugins;
import com.mricefox.archmage.runtime.DependencyInjectionHook;
import com.mricefox.archmage.runtime.DependencyLookupHook;
import com.mricefox.archmage.runtime.HeavyBootTaskAlias;
import com.mricefox.archmage.runtime.IService;
import com.mricefox.archmage.runtime.LifecycleHook;
import com.mricefox.archmage.runtime.LightBootTaskAlias;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/4/22
 */

public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        registerAspectPlugins();
        Archmage.install(this, false);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Archmage.terminate(this);
    }

    private void registerAspectPlugins() {
        ArchmageAspectPlugins.inst().registerDependencyLookupHook(new DependencyLookupHook() {
            @Override
            public boolean onTargetNotFound(Uri uri) {
                return super.onTargetNotFound(uri);
            }

            @Override
            public boolean onServiceNotFound(Class<? extends IService> alias) {
                return super.onServiceNotFound(alias);
            }

            @Override
            public void onBootTaskNotFound(Class<?> alias) {
                super.onBootTaskNotFound(alias);
            }
        });
        ArchmageAspectPlugins.inst().registerDependencyInjectionHook(new DependencyInjectionHook() {
            @Override
            public void onActivatorAttachSuccess(String activatorClassName) {
                Log.d("SampleApplication", "Activator attach success:" + activatorClassName);
            }

            @Override
            public boolean onActivatorAttachFailure(String activatorClassName, Exception e) {
                return super.onActivatorAttachFailure(activatorClassName, e);
            }

            @Override
            public void onModuleRegistered(Class<? extends LightBootTaskAlias> alias) {
                super.onModuleRegistered(alias);
            }

            @Override
            public void onServiceRegistered(Class<? extends IService> alias) {
                super.onServiceRegistered(alias);
            }

            @Override
            public void onTargetRegistered(String group) {
                super.onTargetRegistered(group);
            }
        });
        ArchmageAspectPlugins.inst().registerLifecycleHook(new LifecycleHook() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onDeclareDependencyStart() {
                super.onDeclareDependencyStart();
            }

            @Override
            public void onDeclareDependencyEnd(long nanos) {
                super.onDeclareDependencyEnd(nanos);
            }

            @Override
            public void onFlattenDependencyStart() {
                super.onFlattenDependencyStart();
            }

            @Override
            public void onFlattenDependencyEnd(long nanos) {
                super.onFlattenDependencyEnd(nanos);
            }

            @Override
            public void onLightBootTaskStart(Class<? extends LightBootTaskAlias> alias) {
                super.onLightBootTaskStart(alias);
            }

            @Override
            public void onLightBootTaskEnd(Class<? extends LightBootTaskAlias> alias, long nanos) {
                super.onLightBootTaskEnd(alias, nanos);
            }

            @Override
            public void onAllLightBootTaskDone() {
                super.onAllLightBootTaskDone();
            }

            @Override
            public void onDoInBackgroundStart() {
                super.onDoInBackgroundStart();
            }

            @Override
            public void onHeavyBootTaskDoInBackgroundStart(Class<? extends HeavyBootTaskAlias> alias) {
                super.onHeavyBootTaskDoInBackgroundStart(alias);
            }

            @Override
            public void onHeavyBootTaskDoInBackgroundEnd(Class<? extends HeavyBootTaskAlias> alias, long nanos) {
                super.onHeavyBootTaskDoInBackgroundEnd(alias, nanos);
            }

            @Override
            public void onAllBackgroundTaskDone() {
                super.onAllBackgroundTaskDone();
            }

            @Override
            public void onPostExecuteStart() {
                super.onPostExecuteStart();
            }

            @Override
            public void onHeavyBootTaskPostExecuteStart(Class<? extends HeavyBootTaskAlias> alias) {
                super.onHeavyBootTaskPostExecuteStart(alias);
            }

            @Override
            public void onHeavyBootTaskPostExecuteEnd(Class<? extends HeavyBootTaskAlias> alias, long nanos) {
                super.onHeavyBootTaskPostExecuteEnd(alias, nanos);
            }

            @Override
            public void onAllPostExecuteDone() {
                super.onAllPostExecuteDone();
            }
        });
    }
}
