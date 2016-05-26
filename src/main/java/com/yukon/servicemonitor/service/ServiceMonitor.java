package com.yukon.servicemonitor.service;

import com.yukon.servicemonitor.model.Caller;
import com.yukon.servicemonitor.model.ServiceEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Serhiy Makhov on 23.05.2016.
 * Service monitor that observes services and notifies users about their states.
 */
public class ServiceMonitor {
    private Map<ServiceEntity, List<Caller>> observers = new HashMap<>();

    ServiceMonitor() {
    }
    /**
     * Registers caller for an interest of a service.
     *
     * @param caller        to register
     * @param serviceEntity service in which caller interested
     */
    public void register(ServiceEntity serviceEntity, Caller caller) {
        List<Caller> serviceCallers = observers.get(serviceEntity);
        if (serviceCallers == null) {
            serviceCallers = new ArrayList<>();
            observers.put(serviceEntity,serviceCallers);
        }
        serviceCallers.add(caller);

    }

    /**
     * Notifies callers of specified service
     *
     * @param serviceEntity service about which status we need to notify.
     */
    public void notifyCallers(ServiceEntity serviceEntity, Boolean isUp) {
        observers.get(serviceEntity).stream().forEach(caller -> caller.update(serviceEntity, isUp));
    }

}
