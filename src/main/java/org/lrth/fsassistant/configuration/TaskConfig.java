package org.lrth.fsassistant.configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskConfig {
    private String cron;
    private boolean forceSuspend;
    private boolean simulationMode;
}
