package org.lrth.fsassistant.configuration;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="fs-assistant.file-downloder")
@Getter @Setter
public class FileDownloaderConfig extends BaseTaskConfig {
	public FileDownloaderConfig(VolumesConfig volumesConfigurations) {
		super(volumesConfigurations)
	}
}
