package com.yukon.servicemonitor.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

/**
 * Created by Serhiy Makhov on 23.05.2016.
 * Class that representing user interested in services. Observer pattern.
 */
public class Caller {
    private static final Logger LOGGER = LogManager.getLogger();
    /**
     * Used random UUID hash for caller identifier
     */
    private Integer id = UUID.randomUUID().hashCode();

    /**
     * Action when message about service availability received.
     */
    public void update(ServiceEntity serviceEntity, Boolean isUp) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("User " + id + " received that service " + serviceEntity.toString() + " is ");
        if (isUp) {
            messageBuilder.append("up.");
        } else {
            messageBuilder.append("down.");
        }
        System.out.println(messageBuilder.toString());
    }
}
