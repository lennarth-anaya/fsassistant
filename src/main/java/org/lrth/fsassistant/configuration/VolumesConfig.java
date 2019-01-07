package org.lrth.fsassistant.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "fs-assistant.volumes")
@Getter @Setter
public class VolumesConfig {
    private Map<String, VolumeConfig> volumes;
}
