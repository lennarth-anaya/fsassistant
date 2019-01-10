package org.lrth.fsassistant.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@ConfigurationProperties
@Getter
@Setter
public class TaskConfig {
    @NotNull private String cron;
    private boolean forceSuspend;
    private boolean simulationMode;
    private boolean debugLoggerLevel;
}
