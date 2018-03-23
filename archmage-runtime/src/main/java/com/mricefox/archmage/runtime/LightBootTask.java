package com.mricefox.archmage.runtime;

import android.app.Application;
import android.os.Bundle;

import static com.mricefox.archmage.runtime.Utils.checkNull;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:Light boot task run on ui thread
 * <p>Date:2017/12/25
 */

public abstract class LightBootTask extends BootNode implements LightBootTaskAlias {

    @Override
    protected Class<? extends LightBootTaskAlias> alias() {
        return super.alias();
    }

    public final LightBootTask before(Class<? extends LightBootTaskAlias> alias) {
        checkNull(alias, "Alias");

        LightBootTaskManager.inst().addDependency(alias, alias());
        return this;
    }

    public final LightBootTask after(Class<? extends LightBootTaskAlias> alias) {
        checkNull(alias, "Alias");

        LightBootTaskManager.inst().addDependency(alias(), alias);
        return this;
    }

    /**
     * @return whether boot in other process
     */
    protected boolean bootBesideMainProcess() {
        return false;
    }

    protected abstract void boot(Application application, Bundle extra);
}
