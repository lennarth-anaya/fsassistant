package org.lrth.fsassistant.configuration;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PipeConfig {
    private TaskConfig task;

    private VolumeConfigTaskMeta sourceVolumeMeta;

    private VolumeConfigTaskMeta targetVolumeMeta;

    private VolumesConfig volumesConfigurations;

    public void setSourceVolumeMeta(VolumeConfigTaskMeta sourceVolumeMeta) {
        this.sourceVolumeMeta = sourceVolumeMeta;
        attachVolumeDefToMetadata(sourceVolumeMeta);
    }

    public void setTargetVolumeMeta(VolumeConfigTaskMeta targetVolumeMeta) {
        this.targetVolumeMeta = targetVolumeMeta;
        attachVolumeDefToMetadata(targetVolumeMeta);
    }

    private void attachVolumeDefToMetadata(VolumeConfigTaskMeta volumeMeta) {
        VolumeConfig volumeDef = lookupVolumeDetails(volumeMeta.getVolumeRef());
        volumeMeta.setVolumeDef(volumeDef);
    }

    private VolumeConfig lookupVolumeDetails(String volumeConfigRef) {
        if (this.volumesConfigurations == null) {
            return null;
        }

        return this.volumesConfigurations.getVolumes().get(volumeConfigRef);
    }
}
