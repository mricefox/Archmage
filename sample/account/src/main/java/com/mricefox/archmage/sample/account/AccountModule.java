package com.mricefox.archmage.sample.account;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.mricefox.archmage.annotation.Module;
import com.mricefox.archmage.runtime.ArchmageModule;
import com.mricefox.archmage.runtime.LightBootTaskAlias;
import com.mricefox.archmage.sample.account.export.AccountBootAlias;
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
public class AccountModule extends ArchmageModule {
    @Override
    protected Class<? extends LightBootTaskAlias> alias() {
        return AccountBootAlias.class;
    }

    @Override
    protected void declareBootDependency() {
        //boot after AccountSdkInitTask
        dependsOn(AccountSdkInitTask.class);

        addLightBootTask(
                new AccountSdkInitTask().after(DbSdkInitTask.class).after(NetworkSdkInitTask.class)
        );
    }

    @Override
    protected void boot(Application application, Bundle extra) {
        Log.d(Constants.BOOT_TASK_TAG, "account module boot...");
    }
}
