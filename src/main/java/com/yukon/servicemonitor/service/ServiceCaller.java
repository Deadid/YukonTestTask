package com.yukon.servicemonitor.service;

import com.yukon.servicemonitor.model.ServiceEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Serhiy Makhov on 23.05.2016.
 * Service that calls specified service
 */

public class ServiceCaller {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Checks if specified service is up (trying to establish connection to it).
     */
    public Boolean callService(ServiceEntity serviceEntity) {
        try {
            InetSocketAddress address = new InetSocketAddress(serviceEntity.getIpAddress(), serviceEntity.getPort());
            Socket socket = new Socket();
            socket.connect(address, (int) serviceEntity.getGraceTime().toMillis());
            socket.close();
            return true;
        } catch (IOException e) {
            LOGGER.info(e);
        }
        return false;
    }
}
