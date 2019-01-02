package org.lrth.fsassistant.configuration;

import java.util.List;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="fs-assistant.file-transfer")
@Getter @Setter
public class LocalSourceConfig {
	// attributes not listed here are directly injected via annotations
	@NotNull private String cron;
	@NotNull private String remoteDestinationFolder;
	@NotNull private boolean suspend;
}
