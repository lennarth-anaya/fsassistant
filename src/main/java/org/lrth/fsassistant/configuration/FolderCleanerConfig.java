package org.lrth.fsassistant.configuration;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="fs-assistant.folder-cleaner")
@Getter @Setter
public class FolderCleanerConfig {
    @NotNull private String cron;
    @NotNull private boolean suspend;
}
