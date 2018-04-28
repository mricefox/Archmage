package com.mricefox.archmage.sample.hotel;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.mricefox.archmage.annotation.Module;
import com.mricefox.archmage.runtime.ArchmageModule;
import com.mricefox.archmage.runtime.LightBootTaskAlias;
import com.mricefox.archmage.sample.account.export.AccountBootAlias;
import com.mricefox.archmage.sample.foundation.Constants;
import com.mricefox.archmage.sample.hotel.export.HotelBootAlias;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/4/27
 */
@Module
public class HotelModule extends ArchmageModule {

    @Override
    protected Class<? extends LightBootTaskAlias> alias() {
        return HotelBootAlias.class;
    }

    @Override
    protected void declareBootDependency() {
        dependsOn(HotelSdkInitTask.class);
        dependsOn(AccountBootAlias.class);

        addLightBootTask(new HotelSdkInitTask());
    }

    @Override
    protected void boot(Application application, Bundle extra) {
        Log.d(Constants.BOOT_TASK_TAG, "hotel module boot...");
    }

    @Override
    protected void appTerminate(Application application) {
    }
}
