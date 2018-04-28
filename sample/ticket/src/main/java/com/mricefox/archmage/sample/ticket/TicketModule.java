package com.mricefox.archmage.sample.ticket;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.mricefox.archmage.annotation.Module;
import com.mricefox.archmage.runtime.ArchmageModule;
import com.mricefox.archmage.sample.foundation.Constants;
import com.mricefox.archmage.sample.hotel.export.HotelBootAlias;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/4/27
 */
@Module
public class TicketModule extends ArchmageModule {
    @Override
    protected void declareBootDependency() {
        dependsOn(TicketSdkInitTask.class);
        dependsOn(HotelBootAlias.class);

        addLightBootTask(new TicketSdkInitTask());
    }

    @Override
    protected void boot(Application application, Bundle extra) {
        Log.d(Constants.BOOT_TASK_TAG, "ticket module boot...");
    }
}
