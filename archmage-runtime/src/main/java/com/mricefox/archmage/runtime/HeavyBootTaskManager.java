package com.mricefox.archmage.runtime;


/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/1/4
 */

/*package*/class HeavyBootTaskManager extends BootTaskManager<HeavyBootTask, HeavyBootTaskAlias> {
    private final Logger logger = Logger.getLogger(HeavyBootTaskManager.class);

    private HeavyBootTaskManager() {
    }

    private static final class InstanceHolder {
        private static final HeavyBootTaskManager INSTANCE = new HeavyBootTaskManager();
    }

    static HeavyBootTaskManager inst() {
        return InstanceHolder.INSTANCE;
    }

    //The last heavy boot task after flatten dependencies
    HeavyBootTask getLast() {
        if (!flattened) {
            throw new ArchmageException("Should call flattenDependency() first");
        }

        if (!flattenDependencies.isEmpty()) {
            return flattenDependencies.getLast();
        }
        return null;
    }

    //The first heavy boot task after flatten dependencies
    HeavyBootTask getFirst() {
        if (!flattened) {
            throw new ArchmageException("Should call flattenDependency() first");
        }
        if (!flattenDependencies.isEmpty()) {
            return flattenDependencies.getFirst();
        }
        return null;
    }
}
