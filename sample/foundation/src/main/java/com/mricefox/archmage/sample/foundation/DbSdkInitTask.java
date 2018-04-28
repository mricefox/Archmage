package com.mricefox.archmage.sample.foundation;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.mricefox.archmage.runtime.LightBootTask;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description: db sdk init task
 * <p>Date:2018/4/27
 */

public class DbSdkInitTask extends LightBootTask {
    @Override
    protected void boot(Application application, Bundle extra) {
        Log.d(Constants.BOOT_TASK_TAG, "db sdk boot...");
    }
}
