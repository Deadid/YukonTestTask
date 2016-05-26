package com.yukon.servicemonitor.service;

import com.yukon.servicemonitor.model.Caller;
import com.yukon.servicemonitor.model.ServiceEntity;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by Serhiy Makhov on 26.05.2016.
 */
public class IntegrationTest {
    private static final MonitorFacade monitorFacade = new MonitorFacade();

    private static ServerSocket serverSocket;

    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private Caller testCaller = new Caller();
    private static ServiceEntity testEntity;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.setOut(new PrintStream(outContent));
        serverSocket = new ServerSocket(50000);
        testEntity = new ServiceEntity(InetAddress.getByName("127.0.0.1"), 50000, 2);

    }
    @Test
    public void testCaller() throws  Exception {
        monitorFacade.registerCaller(testEntity, testCaller);
        Thread.sleep(3000);
        assertTrue(outContent.toString().contains("ServiceEntity{port=50000, ipAddress=/127.0.0.1} is up."));

    }
    @AfterClass
    public static void tearDownClass() throws Exception {
        serverSocket.close();
        System.setOut(null);
    }
}
