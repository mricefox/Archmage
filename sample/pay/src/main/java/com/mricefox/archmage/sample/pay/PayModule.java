package com.mricefox.archmage.sample.pay;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.mricefox.archmage.annotation.Module;
import com.mricefox.archmage.runtime.ArchmageModule;
import com.mricefox.archmage.sample.foundation.Constants;
import com.mricefox.archmage.sample.foundation.DbSdkInitTask;
import com.mricefox.archmage.sample.foundation.NetworkSdkInitTask;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/4/27
 */
@Module
public class PayModule extends ArchmageModule {

    @Override
    protected void declareBootDependency() {
        dependsOn(DbSdkInitTask.class);
        dependsOn(NetworkSdkInitTask.class);
    }

    @Override
    protected void boot(Application application, Bundle extra) {
        Log.d(Constants.BOOT_TASK_TAG, "pay module boot...");
    }
}
