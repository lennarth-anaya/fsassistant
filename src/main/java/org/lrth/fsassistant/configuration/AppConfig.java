package org.lrth.fsassistant.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix="fs-assistant")
@Getter @Setter
public class AppConfig {
    private VolumesConfig volumes;
    private List<PipeConfig> pipesConfig;
}
