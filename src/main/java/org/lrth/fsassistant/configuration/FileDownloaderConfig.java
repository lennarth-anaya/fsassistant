package org.lrth.fsassistant.configuration;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

// TODO: get rid of this, move it to integration so developer has to deal with the less
@ConfigurationProperties(prefix="fs-assistant.file-downloder")
@Getter @Setter
public class FileDownloaderConfig extends PipeConfig {
	public FileDownloaderConfig(VolumesConfig volumesConfigurations) {
		super(volumesConfigurations);
	}
}
