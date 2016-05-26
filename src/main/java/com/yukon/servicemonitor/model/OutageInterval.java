package com.yukon.servicemonitor.model;

import java.time.LocalDateTime;

/**
 * Created by Serhiy Makhov on 25.05.2016.
 * There is no equivalent in JSR-310 to JodaTime-Interval-class. So I've implemented mine. Immutable.
 */
public class OutageInterval {
    private LocalDateTime outageStart;
    private LocalDateTime outageEnd;

    public OutageInterval(LocalDateTime outageStart, LocalDateTime outageEnd) {
        this.outageStart = outageStart;
        this.outageEnd = outageEnd;
        if (outageStart.isAfter(outageEnd)) {
            throw new IllegalArgumentException("Start date is after end date!");
        }
    }

    public LocalDateTime getOutageStart() {
        return outageStart;
    }

    public LocalDateTime getOutageEnd() {
        return outageEnd;
    }
}
