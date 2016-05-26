package com.yukon.servicemonitor.service;

import com.yukon.servicemonitor.model.Caller;
import com.yukon.servicemonitor.model.OutageInterval;
import com.yukon.servicemonitor.model.ServiceEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.quartz.Scheduler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Created by Serhiy Makhov on 26.05.2016.
 */
public class MonitorFacadeTest {
    private static ServerSocket serverSocket;
    private final MonitorFacade monitorFacade = new MonitorFacade();

    private Caller testCaller = new Caller();
    private ServiceEntity testEntity;

    @Before
    public void setUp() throws Exception {
        Field[] fields = MonitorFacade.class.getDeclaredFields();

        Field serviceCallerSchedulerField = Arrays.stream(fields)
                .filter(field -> field.getType().equals(ServiceCallerScheduler.class))
                .findFirst()
                .get();
        serviceCallerSchedulerField.setAccessible(true);
        ServiceCallerScheduler serviceCallerScheduler = mock(ServiceCallerScheduler.class);
        doThrow(new RuntimeException())
                .when(serviceCallerScheduler).registerService(any(ServiceEntity.class));
        doThrow(new RuntimeException())
                .when(serviceCallerScheduler).registerOutage(any(ServiceEntity.class), any(OutageInterval.class));
        serviceCallerSchedulerField.set(monitorFacade, serviceCallerScheduler);
        serverSocket = new ServerSocket(50000);
        testEntity = new ServiceEntity(InetAddress.getByName("127.0.0.1"), 50000, 2);

    }

    @After
    public void tearDown() throws Exception {
        serverSocket.close();
    }


    @Test(expected = RuntimeException.class)
    public void testRegisterCaller() throws Exception {
        monitorFacade.registerCaller(testEntity, testCaller);
    }

    @Test(expected =  RuntimeException.class)
    public void testRegisterOutage() throws Exception {
        monitorFacade.registerOutage(testEntity, new OutageInterval(LocalDateTime.MIN, LocalDateTime.MAX));
    }

}