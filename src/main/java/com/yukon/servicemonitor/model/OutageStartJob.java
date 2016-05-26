package com.yukon.servicemonitor.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;

/**
 * Created by Serhiy Makhov on 25.05.2016.
 * Pauses calls for service.
 */
public class OutageStartJob implements Job {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Scheduler scheduler = jobExecutionContext.getScheduler();
        JobKey jobKey = (JobKey) jobExecutionContext.get("jobKey");
        try {
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            LOGGER.error(e);
        }
    }
}
