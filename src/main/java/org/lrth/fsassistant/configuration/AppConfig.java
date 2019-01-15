package org.lrth.fsassistant.configuration;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix="fs-assistant")
@Getter
public class AppConfig {
    private Map<String, VolumeConfig> volumes;
    private Map<String, PipeConfig> pipes;

    public void setPipes(Map<String, PipeConfig> pipes) {
        this.pipes = pipes;
        attachVolumes();
    }

    public void setVolumes(Map<String, VolumeConfig> volumes) {
        this.volumes = volumes;
        attachVolumes();;
    }

    private void attachVolumes() {
        if (pipes != null && volumes != null) {
            pipes.entrySet().forEach(e -> {
                PipeConfig pipeConfig = e.getValue();
                VolumeConfigTaskMeta meta;

                meta = pipeConfig.getSourceVolumeMeta();
                if (meta != null) {
                    meta.setVolumeDef(volumes.get(meta.getVolumeRef()));
                }

                meta = pipeConfig.getTargetVolumeMeta();
                if (meta != null) {
                    meta.setVolumeDef(volumes.get(meta.getVolumeRef()));
                }
            });
        }
    }
}
