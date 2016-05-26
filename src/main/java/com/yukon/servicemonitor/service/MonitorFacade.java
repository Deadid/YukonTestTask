package com.yukon.servicemonitor.service;

import com.yukon.servicemonitor.model.Caller;
import com.yukon.servicemonitor.model.OutageInterval;
import com.yukon.servicemonitor.model.ServiceEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.SchedulerException;

/**
 * Created by Serhiy Makhov on 26.05.2016.
 * Class for using all written classes. Facade pattern.
 */
public class MonitorFacade {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ServiceMonitor monitor;
    private ServiceCallerScheduler serviceCallerScheduler = new ServiceCallerScheduler();

    public MonitorFacade() {
        monitor = serviceCallerScheduler.getServiceMonitor();
    }

    /**
     * Registers a new caller which interested in service.
     * @param service of interest.
     * @param caller interested in service.
     */
    public void registerCaller(ServiceEntity service, Caller caller) {
        try {
            serviceCallerScheduler.registerService(service);
            monitor.register(service, caller);
        } catch (SchedulerException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Registers outage for service.
     * @param serviceEntity which have outage.
     * @param interval time interval of service outage.
     */
    public void registerOutage(ServiceEntity serviceEntity, OutageInterval interval) {
        try {
            serviceCallerScheduler.registerOutage(serviceEntity, interval);
        } catch (SchedulerException e) {
            LOGGER.error(e);
        }
    }
}
