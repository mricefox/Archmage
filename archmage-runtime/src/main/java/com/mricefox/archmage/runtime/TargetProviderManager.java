package com.mricefox.archmage.runtime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.mricefox.archmage.runtime.Utils.checkEmpty;
import static com.mricefox.archmage.runtime.Utils.checkNull;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/1/18
 */

/*package*/ class TargetProviderManager {
    private Map<String, ITargetProvider> transferProviderCache = new ConcurrentHashMap<>();

    private TargetProviderManager() {
    }

    private static final class InstanceHolder {
        private static final TargetProviderManager INSTANCE = new TargetProviderManager();
    }

    public static TargetProviderManager inst() {
        return InstanceHolder.INSTANCE;
    }

    void registerTargetProvider(String group, ITargetProvider provider) {
        checkEmpty(group, "Group");
        checkNull(provider, "TargetProvider");

        if (transferProviderCache.containsKey(group)) {
            throw new IllegalArgumentException("Duplicate group:" + group);
        }

        if (transferProviderCache.containsValue(provider)) {
            throw new IllegalArgumentException("Provider already registered");
        }

        transferProviderCache.put(group, provider);
    }

    @SuppressWarnings("unchecked")
    <T> Class<T> findTarget(Class<? super T> parent, String group, String path) {
        checkNull(parent, "Parent class");
        checkEmpty(group, "Group");
        checkEmpty(path, "Path");

        ITargetProvider provider = transferProviderCache.get(group);

        if (provider == null) {
            throw new TargetNotFoundException("Group:" + group + " not found");
        }

        Class c = provider.bindTargets(path);

        //c is sub class of parent
        if (c != null && parent.isAssignableFrom(c)) {
            return c;
        } else {
            throw new TargetNotFoundException(group + "/" + path + " path not found");
        }
    }
}
