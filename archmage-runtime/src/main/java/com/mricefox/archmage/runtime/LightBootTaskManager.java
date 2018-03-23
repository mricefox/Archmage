package com.mricefox.archmage.runtime;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/12/25
 */

/*package*/ class LightBootTaskManager extends BootTaskManager<LightBootTask, LightBootTaskAlias> {
    private final Logger logger = Logger.getLogger(LightBootTaskManager.class);

    private LightBootTaskManager() {
    }

    private static final class InstanceHolder {
        private static final LightBootTaskManager INSTANCE = new LightBootTaskManager();
    }

    static LightBootTaskManager inst() {
        return InstanceHolder.INSTANCE;
    }
}
