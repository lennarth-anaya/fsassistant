package org.lrth.fsassistant.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "fs-assistant.volumes")
@Getter @Setter
public class VolumesConfig {
    private Map<String, VolumeConfig> volumes;
}
