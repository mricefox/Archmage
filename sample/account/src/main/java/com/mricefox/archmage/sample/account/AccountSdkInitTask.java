package com.mricefox.archmage.sample.account;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.mricefox.archmage.runtime.LightBootTask;
import com.mricefox.archmage.sample.foundation.Constants;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/4/27
 */

public class AccountSdkInitTask extends LightBootTask {
    @Override
    protected void boot(Application application, Bundle extra) {
        Log.d(Constants.BOOT_TASK_TAG, "account sdk boot...");
    }
}
