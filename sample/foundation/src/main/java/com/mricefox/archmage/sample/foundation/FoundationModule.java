package com.mricefox.archmage.sample.foundation;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.mricefox.archmage.annotation.Module;
import com.mricefox.archmage.runtime.ArchmageModule;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/4/27
 */
@Module
public class FoundationModule extends ArchmageModule {

    @Override
    protected void declareBootDependency() {
        addLightBootTask(
                new DbSdkInitTask(),
                new NetworkSdkInitTask(),
                new LocationSdkInitTask().after(DbSdkInitTask.class).after(NetworkSdkInitTask.class),
                new MonitorSdkInit().after(DbSdkInitTask.class).after(NetworkSdkInitTask.class)
        );

        addHeavyBootTask(new PushSdkInitTask());
    }

    @Override
    protected void boot(Application application, Bundle extra) {
        Log.d(Constants.BOOT_TASK_TAG, "foundation module boot...");
    }
}
