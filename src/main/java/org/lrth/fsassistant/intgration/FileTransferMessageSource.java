package org.lrth.fsassistant.integration;

import java.util.Optional;

import java.io.File;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.filters.CompositeFileListFilter;

@Configuration
@RequiredArgsConstructor
public class FileTransferMessageSource {
	
	private FileMessageSourceFactory messageSourceFactory;
	private LocalSourceConfig localSourceConfig;
	private FileTransferConfig fileTransferConfig;
	
   @Bean
   @InboundChannelAdapter(
      value = "${fs-assistant.file-transfer.inputChannelId}",
      // poller = @Poller(cron = "${file-transfer.cron}")
      poller = @Poller(trigger = "fileTransferTrigger")
   )
   public MessageSource<File> getFileReadingMessageSource() {
   	
      CompositeFileListFilter<File> filters = new CompositeFileListFilter<>();
      // add all extensions from configuration
      fileTransferConfig.forEach(ext -> filters.addFilter(new SimplePatternFileListFilter(ext)));
      // TODO
      filters.addFilter(new LastModifiedFileFilter());

		return messageSourceFactory.createFileMessageSource(
		   localSourceConfig.getFolder(), Optional.of(filters));
   }
   
}
