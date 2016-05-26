package com.yukon.servicemonitor.service;

import com.yukon.servicemonitor.job.OutageEndJob;
import com.yukon.servicemonitor.job.OutageStartJob;
import com.yukon.servicemonitor.job.ServiceCallJob;
import com.yukon.servicemonitor.model.OutageInterval;
import com.yukon.servicemonitor.model.ServiceEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Serhiy Makhov on 24.05.2016.
 * Register and schedule services calls
 */

public class ServiceCallerScheduler {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Pair of ServiceEntities with JobKeys for scheduler
     */
    private Map<ServiceEntity, JobKey> serviceEntitiesMap;
    private final ServiceMonitor serviceMonitor = new ServiceMonitor();
    private Scheduler scheduler;

    ServiceCallerScheduler() {
        serviceEntitiesMap = new HashMap<>();
        try {
            scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            LOGGER.fatal(e);
        }
    }

    /**
     * Registers a new service for a call. Or re-register existing if new poll frequency is less than current
     *
     * @param serviceEntity service of interested
     */
    public void registerService(ServiceEntity serviceEntity) throws SchedulerException {
        JobKey jobKey = serviceEntitiesMap.get(serviceEntity);
        if (jobKey == null) {
            createNewJob(serviceEntity);
        } else {
            updateExistingJob(jobKey, serviceEntity);
        }

    }


    /**
     * Registers outage period for service.
     *
     * @param serviceEntity Service for outage.
     * @param interval      DateTime interval for outage.
     * @throws SchedulerException
     */
    public void registerOutage(ServiceEntity serviceEntity, OutageInterval interval) throws SchedulerException {
        LocalDateTime start = interval.getOutageStart();
        LocalDateTime end = interval.getOutageEnd();
        Trigger outageStartTrigger = createTriggerForOutageJob(start);
        Trigger outageEndTrigger = createTriggerForOutageJob(end);

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobKey", serviceEntitiesMap.get(serviceEntity));

        JobDetail startOutageJobDetail = JobBuilder.newJob(OutageStartJob.class).setJobData(jobDataMap).build();
        scheduler.scheduleJob(startOutageJobDetail, outageStartTrigger);

        JobDetail endOutageJobDetail = JobBuilder.newJob(OutageEndJob.class).setJobData(jobDataMap).build();
        scheduler.scheduleJob(endOutageJobDetail, outageEndTrigger);
    }

    private Trigger createTriggerForOutageJob(LocalDateTime jobDateTime) {
        Date jobDate = Date.from(jobDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return TriggerBuilder.newTrigger()
                .startAt(jobDate)
                .withSchedule(SimpleScheduleBuilder
                        .simpleSchedule()
                        .withIntervalInHours(jobDateTime.getHour())
                        .withIntervalInMinutes(jobDateTime.getMinute())
                        .withIntervalInSeconds(jobDateTime.getSecond()))
                .build();
    }

    /**
     * Creates a new job for scheduler.
     *
     * @param serviceEntity which job have to call
     * @throws SchedulerException
     */
    private void createNewJob(ServiceEntity serviceEntity) throws SchedulerException {
        Trigger jobTrigger = TriggerBuilder.newTrigger()
                .withSchedule(SimpleScheduleBuilder
                        .simpleSchedule().withIntervalInSeconds(serviceEntity.getPollingFrequency())
                        .repeatForever()).withIdentity(serviceEntity.toString()).startNow()
                .startNow()
                .build();
        JobDataMap jobData = new JobDataMap();
        jobData.put("monitor", serviceMonitor);
        jobData.put("serviceEntity", serviceEntity);
        JobDetail jobDetail = JobBuilder.newJob(ServiceCallJob.class).setJobData(jobData).build();
        serviceEntitiesMap.put(serviceEntity, jobDetail.getKey());
        scheduler.scheduleJob(jobDetail, jobTrigger);

    }

    /**
     * Updates existing job for serviceEntity, if new polling frequency is more than old, then do nothing.
     *
     * @param jobKey        Job to update key for scheduler.
     * @param serviceEntity service entity with new parameters.
     * @throws SchedulerException
     */
    private void updateExistingJob(JobKey jobKey, ServiceEntity serviceEntity) throws SchedulerException {
        JobDataMap jobData = scheduler.getJobDetail(jobKey).getJobDataMap();
        ServiceEntity oldServiceEntity = (ServiceEntity) jobData.get("serviceEntity");
        Integer pollingFrequency = serviceEntity.getPollingFrequency();
        if (pollingFrequency < oldServiceEntity.getPollingFrequency()) {
            Trigger oldTrigger = scheduler.getTriggersOfJob(jobKey).get(0);
            TriggerBuilder builder = oldTrigger.getTriggerBuilder();
            Trigger newTrigger = builder
                    .withSchedule(SimpleScheduleBuilder
                            .simpleSchedule()
                            .repeatForever()
                            .withIntervalInSeconds(pollingFrequency)).startNow()
                    .startNow()
                    .build();
            scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
        }
    }

    public ServiceMonitor getServiceMonitor() {
        return serviceMonitor;
    }
}
