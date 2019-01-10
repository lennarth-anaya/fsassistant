package org.lrth.fsassistant.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;

@Getter @Setter
public class PipeConfig {
    @NotNull @Autowired
    private TaskConfig task;

    @Autowired
    private VolumeConfigTaskMeta sourceVolumeMeta;

    @Autowired
    private VolumeConfigTaskMeta targetVolumeMeta;

    @Autowired
    private VolumesConfig volumesConfigurations;

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
