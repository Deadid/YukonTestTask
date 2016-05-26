package com.yukon.servicemonitor.model;

import com.yukon.servicemonitor.service.ServiceCaller;
import com.yukon.servicemonitor.service.ServiceMonitor;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by Serhiy Makhov on 24.05.2016.
 * Job that calls specified service.
 */

public class ServiceCallJob implements Job {
    private static final ServiceCaller SERVICE_CALLER = new ServiceCaller();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap data =jobExecutionContext.getJobDetail().getJobDataMap();
        ServiceEntity serviceEntity = (ServiceEntity) data.get("serviceEntity");
        Boolean isUp = SERVICE_CALLER.callService(serviceEntity);
        ServiceMonitor monitor = (ServiceMonitor) data.get("monitor");
        monitor.notifyCallers(serviceEntity, isUp);
    }
}
