package com.yukon.servicemonitor.service;

import com.yukon.servicemonitor.model.Caller;
import com.yukon.servicemonitor.model.ServiceEntity;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import  static org.junit.Assert.*;

/**
 * Created by Serhiy Makhov on 25.05.2016.
 */
public class ServiceMonitorTest {

    private static final ServiceMonitor SERVICE_MONITOR = new ServiceMonitor();
    private Map<ServiceEntity, List<Caller>> observers;
    private Caller testCaller1ForService1;
    private ServiceEntity entityForCaller1Service1;
    private Caller testCaller2ForService1;
    private ServiceEntity entityForCaller2Service1;
    private Caller testCaller1ForService2;
    private ServiceEntity entityForCaller1Service2;

    @Before
    public void setUp() throws Exception {
        Field[] fields = ServiceMonitor.class.getDeclaredFields();
        Field observerField = Arrays.stream(fields).filter(field -> field.getType().equals(Map.class)).findFirst().get();
        observerField.setAccessible(true);
        observers =(Map) observerField.get(SERVICE_MONITOR);
        testCaller1ForService1 = mock(Caller.class);
        entityForCaller1Service1 = new ServiceEntity(InetAddress.getByName("127.0.0.1"),8080, 20);
        testCaller2ForService1 = mock(Caller.class);
        entityForCaller2Service1 = new ServiceEntity(InetAddress.getByName("127.0.0.1"),8080, 30);
        testCaller1ForService2 = mock(Caller.class);
        entityForCaller1Service2 = new ServiceEntity(InetAddress.getByName("127.0.0.1"),8081, 30);
        doThrow(new RuntimeException()).when(testCaller1ForService2).update(any(ServiceEntity.class), anyBoolean());

    }
    @After
    public void tearDown() {
        observers.clear();

    }
    @Test
    public void testRegister() throws Exception {

        SERVICE_MONITOR.register(entityForCaller1Service1, testCaller1ForService1);
        SERVICE_MONITOR.register(entityForCaller2Service1, testCaller2ForService1);
        SERVICE_MONITOR.register(entityForCaller1Service2, testCaller1ForService2);
        assertEquals(2, observers.size());
        assertEquals(2, observers.get(entityForCaller1Service1).size());
        assertEquals(1, observers.get(entityForCaller1Service2).size());
    }

    @Test(expected = RuntimeException.class)
    public void testNotifyCallers() throws Exception {
        Logger logger = mock(Logger.class);

        List<Caller> listOfCallersForService1 = new ArrayList<>();
        listOfCallersForService1.add(testCaller1ForService1);
        listOfCallersForService1.add(testCaller2ForService1);
        observers.put(entityForCaller1Service1, listOfCallersForService1);
        List<Caller> listOfCallersForService2 = new ArrayList<>();
        listOfCallersForService2.add(testCaller1ForService2);
        observers.put(entityForCaller1Service2, listOfCallersForService2);
        SERVICE_MONITOR.notifyCallers(entityForCaller1Service2, true);
    }
}
