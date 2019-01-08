package org.lrth.fsassistant.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix="fs-assistant.folder-cleaner")
@Getter @Setter
public class FolderCleanerConfig extends BaseTaskConfig {
    @NotNull private TaskConfig task;
    @NotNull private VolumeConfigTaskMeta targetVolume;

    @NotNull private String channel;

    public FolderCleanerConfig(VolumesConfig volumesConfigurations) {
        super(volumesConfigurations)
    }

    public VolumeConfig getSourceVolumeConfig() {
        throw new IllegalArgumentException("Folder cleaner task does not use a source volume");
    }

    public VolumeConfigTaskMeta getSourceVolumeMeta() {
        throw new IllegalArgumentException("Folder cleaner task does not use a source volume metadata");
    }
}
