package com.mricefox.archmage.runtime;

import android.net.Uri;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/1/23
 */

public abstract class DependencyLookupHook {

    /**
     * @return true-consumed error, false-further process, throw exception or invoke callback
     */
    public boolean onTargetNotFound(Uri uri) {
        return false;
    }

    /**
     * @return true-consumed error, false-further process, throw exception or invoke callback
     */
    public boolean onServiceNotFound(Class<? extends IService> alias) {
        return false;
    }

    public void onBootTaskNotFound(Class<?> alias) {
    }
}
