package com.mricefox.archmage.runtime;

import android.app.Application;

import static com.mricefox.archmage.runtime.Utils.checkNull;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:Boot node represent a project
 * <p>Date:2017/12/26
 */

public abstract class ArchmageModule extends LightBootTask implements LightBootTaskAlias {
    private final Logger logger = Logger.getLogger(ArchmageModule.class);

    protected void declareBootDependency() {
    }

    protected void appTerminate(Application application) {
    }

    @Override
    protected Class<? extends LightBootTaskAlias> alias() {
        return super.alias();
    }

    protected final void dependsOn(Class<? extends LightBootTaskAlias> alias) {
        checkNull(alias, "Alias");

        LightBootTaskManager.inst().addDependency(alias(), alias);
    }

    protected final void addLightBootTask(LightBootTask... bootTasks) {
        checkNull(bootTasks, "Boot task array");

        for (LightBootTask task : bootTasks) {
            checkNull(task, "Boot task");
            LightBootTaskManager.inst().addDependencyNode(task.alias(), task);
        }
    }

    protected final void addHeavyBootTask(HeavyBootTask... tasks) {
        if (tasks == null) {
            return;
        }

        for (HeavyBootTask task : tasks) {
            checkNull(task, "Boot task");
            HeavyBootTaskManager.inst().addDependencyNode(task.alias(), task);
        }
    }
}
