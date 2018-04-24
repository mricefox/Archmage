package com.mricefox.archmage.sample.app;

import android.app.Application;

import com.mricefox.archmage.runtime.Archmage;

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
        Archmage.install(this, false);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Archmage.terminate(this);
    }
}
