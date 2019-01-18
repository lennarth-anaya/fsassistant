package org.lrth.fsassistant.configuration;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@Component
@ManagedResource
public class Test {
    @ManagedAttribute
    public String getX() {
        return x;
    }

    @ManagedAttribute
    public void setX(String x) {
        this.x = x;
    }

    private String x;
}
