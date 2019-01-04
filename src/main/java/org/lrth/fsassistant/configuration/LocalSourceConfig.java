package org.lrth.fsassistant.configuration;

import java.util.List;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="fs-assistant.local-source")
@Getter @Setter
public class LocalSourceConfig {
	@NotNull private String folder;
	@NotNull private List<String> fileExtensions;
	@NotNull private Integer maxExpectedFiles;
}
