package com.mricefox.archmage.runtime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.mricefox.archmage.runtime.Utils.checkNull;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/12/14
 */

/*package*/ class ServiceManager {
    private final Map<Class<? extends IService>, IService> serviceCache = new ConcurrentHashMap<>();
    private final DependencyLookupHook dlHook = ArchmageAspectPlugins.inst().getDependencyLookupHook();

    private ServiceManager() {
    }

    private static final class InstanceHolder {
        private static final ServiceManager INSTANCE = new ServiceManager();
    }

    static ServiceManager inst() {
        return InstanceHolder.INSTANCE;
    }

    <Service extends IService> void registerService(Class<Service> alias, Service service) {
        checkNull(alias, "Alias");
        checkNull(alias, "Service");

        if (serviceCache.containsKey(alias)) {
            throw new IllegalArgumentException("Duplicate alias:" + alias);
        }

        if (serviceCache.containsValue(service)) {
            throw new IllegalArgumentException("Service:" + service + " already registered");
        }
        serviceCache.put(alias, service);
    }

    /**
     * @throws ServiceNotFoundException
     */
    @SuppressWarnings("unchecked")
    <Service extends IService> Service findService(Class<Service> alias) {
        checkNull(alias, "Alias");

        Service service = (Service) serviceCache.get(alias);

        if (service == null) {
            if (!dlHook.onServiceNotFound(alias)) {
                throw new ServiceNotFoundException("Service with alias:" + alias + " not found");
            }
        }
        return service;
    }

    @SuppressWarnings("unchecked")
    <Service extends IService> void findService(Class<Service> alias, ServiceFindCallback<Service> callback) {
        checkNull(alias, "Alias");
        checkNull(callback, "ServiceFindCallback");

        Service service = (Service) serviceCache.get(alias);

        if (service == null) {
            if (!dlHook.onServiceNotFound(alias)) {
                callback.notFound(alias);
            }
        } else {
            callback.found(service);
        }
    }
}
