package com.yukon.servicemonitor.model;

import java.net.InetAddress;
import java.time.Duration;

/**
 * Created by Serhiy Makhov on 23.05.2016.
 * Entity that represents Service with IP address, port and parameters of calls.
 */
public class ServiceEntity {

    /** Ip address of service*/
    private InetAddress ipAddress;
    /** Service port*/
    private Integer port;
    /** Polling frequency*/
    private Integer pollingFrequency;
    /** Grace time duration*/
    private Duration graceTime;


    public ServiceEntity(InetAddress ipAddress, Integer port, Integer pollingFrequency) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.pollingFrequency = pollingFrequency;
        /**Default grace time equals 1sec (as we cant call service more than 1 time per second)*/
        this.graceTime = Duration.ofSeconds(1);
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getPollingFrequency() {
        return pollingFrequency;
    }

    public void setPollingFrequency(Integer pollingFrequency) {
        this.pollingFrequency = pollingFrequency;
    }

    public Duration getGraceTime() {
        return graceTime;
    }

    public void setGraceTime(Duration graceTime) {
        this.graceTime = graceTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceEntity)) return false;
        ServiceEntity that = (ServiceEntity) o;
        if (!ipAddress.equals(that.ipAddress)) return false;
        return port != null ? port.equals(that.port) : that.port == null;

    }

    @Override
    public int hashCode() {
        int result = ipAddress.hashCode();
        result = 31 * result + port.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ServiceEntity{" +
                "port=" + port +
                ", ipAddress=" + ipAddress +
                '}';
    }
}
