package org.lrth.fsassistant.configuration;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="fs-assistant.file-uploader")
@Getter @Setter
public class FileUploaderConfig extends BaseTaskConfig {
    public FileUploaderConfig(VolumesConfig volumesConfigurations) {
        super(volumesConfigurations);
    }
}
