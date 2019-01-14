package org.lrth.fsassistant.configuration;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PipeConfig {
    private TaskConfig task;
    private VolumeConfigTaskMeta sourceVolumeMeta;
    private VolumeConfigTaskMeta targetVolumeMeta;
}
