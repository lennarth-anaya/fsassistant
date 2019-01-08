package org.lrth.fsassistant.configuration;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter @Setter
public class BaseTaskConfig {
    @NotNull private TaskConfig task;
    @NotNull private VolumeConfigTaskMeta sourceVolumeMeta;
    @NotNull private VolumeConfigTaskMeta targetVolumeMeta;

    @NotNull private String channel;

    private VolumesConfig volumesConfigurations;

    public BaseTaskConfig(VolumesConfig volumesConfigurations) {
        this.volumesConfigurations = volumesConfigurations;
    }

    public VolumeConfig getSourceVolumeConfig() {
        return lookupVolumeDetails(this.sourceVolumeMeta.getVolumeRef());
    }

    public VolumeConfig getTargetVolumeConfig() {
        return lookupVolumeDetails(this.targetVolumeMeta.getVolumeRef());
    }

    private VolumeConfig lookupVolumeDetails(String volumeConfigRef) {
        return this.volumesConfigurations.getVolumes().get(volumeConfigRef);
    }
}
