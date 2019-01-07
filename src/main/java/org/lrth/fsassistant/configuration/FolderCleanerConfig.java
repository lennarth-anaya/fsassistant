package org.lrth.fsassistant.configuration;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="fs-assistant.folder-cleaner")
@Getter @Setter
public class FolderCleanerConfig {
    @NotNull private TaskConfig task;
    @NotNull private VolumeConfigRef targetVolume;

    @NotNull private String channel;
}
