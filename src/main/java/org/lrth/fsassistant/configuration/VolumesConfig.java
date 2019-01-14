package org.lrth.fsassistant.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter @Setter
public class VolumesConfig {
    private Map<String, VolumeConfig> volumes;
}
