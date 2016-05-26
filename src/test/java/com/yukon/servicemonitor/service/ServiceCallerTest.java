package com.yukon.servicemonitor.service;

import com.yukon.servicemonitor.model.ServiceEntity;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;

/**
 * Created by Serhiy Makhov on 23.05.2016.
 */

public class ServiceCallerTest {
    private static ServerSocket serverSocket;
    private final ServiceCaller serviceCaller = new ServiceCaller();

    @BeforeClass
    public static void setUp() throws IOException {
        serverSocket = new ServerSocket(50000);
    }

    @AfterClass
    public static void tearDown() throws IOException {
        serverSocket.close();
    }

    @Test
    public void localHostTest() throws UnknownHostException {
        /** Connect to opened port*/
        assertEquals(true, serviceCaller.callService(new ServiceEntity(InetAddress.getByName("127.0.0.1"), 50000, 10)));
        /** Connect to closed port */
        assertEquals(false, serviceCaller.callService(new ServiceEntity(InetAddress.getByName("127.0.0.1"), 49999, 10)));
    }

}
