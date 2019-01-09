package org.lrth.fsassistant.configuration;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter @Setter
public class PipeConfig {
    @NotNull private TaskConfig task;
    @NotNull private VolumeConfigTaskMeta sourceVolumeMeta;
    @NotNull private VolumeConfigTaskMeta targetVolumeMeta;

    private VolumesConfig volumesConfigurations;

    public PipeConfig(VolumesConfig volumesConfigurations) {
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
