package com.mricefox.archmage.sample.foundation;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.mricefox.archmage.runtime.HeavyBootTask;

import java.util.concurrent.TimeUnit;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/4/28
 */

public class PushSdkInitTask extends HeavyBootTask<String> {

    @Override
    protected String doInBackground(Application application, Bundle extra) {
        Log.d(Constants.BOOT_TASK_TAG, "push sdk boot, run in thread:" + Thread.currentThread().getName());
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Channel_X";
    }

    @Override
    protected void onPostExecute(Application application, String s) {
        Log.d(Constants.BOOT_TASK_TAG, "push sdk boot, bg result:" + s);
        Log.d(Constants.BOOT_TASK_TAG, "push sdk boot, run in thread:" + Thread.currentThread().getName());
    }

    @Override
    protected boolean bootBesideMainProcess() {
        return false;
    }
}
