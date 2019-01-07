package org.lrth.fsassistant.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
@Getter
@Setter
public class TaskConfig {
    private String cron;
    private boolean forceSuspend;
    private boolean simulationMode;
    private boolean debugLoggerLevel;
}
