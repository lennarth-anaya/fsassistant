package org.lrth.fsassistant.configuration;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="fs-assistant.file-transfer")
@Getter @Setter
public class FileTransferConfig {
	@NotNull private TaskConfig task;
	@NotNull private VolumeConfigRef sourceVolume;
	@NotNull private VolumeConfigRef targetVolume;

	@NotNull private String channel;
}
