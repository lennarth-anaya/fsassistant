package org.lrth.fsassistant.integration;

import java.util.Optional;

import java.io.File;

import lombok.RequiredArgsConstructor;

import org.lrth.fsassistant.configuration.FileTransferConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.filters.ChainFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.sftp.filters.SftpPersistentAcceptOnceFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;

import javax.validation.constraints.NotNull;

@Configuration
@RequiredArgsConstructor
public class FileTransferMessageSource {
	
    @NotNull private FileMessageSourceFactory messageSourceFactory;
    @NotNull private FileTransferConfig config;

    @Bean
    @InboundChannelAdapter(
        value = "${fs-assistant.file-transfer.inputChannelId}",
        poller = @Poller(trigger = "fileTransferTrigger")
    )
    public MessageSource<File> getFileReadingMessageSource() {

        SftpInboundFileSynchronizingMessageSource source =
                new SftpInboundFileSynchronizingMessageSource(s);
        source.setLocalDirectory(new File("ftp-inbound"));
        source.setAutoCreateLocalDirectory(true);
        source.setLocalFilter(new AcceptOnceFileListFilter<File>());
        return source;

        ChainFileListFilter<File> filters = new ChainFileListFilter<>();

        // add all file extensions from configuration
        localSourceConfig.getFileExtensions().forEach(ext -> filters.addFilter(
            new SimplePatternFileListFilter(ext)));

        filters.addFilter(new SftpPersistentAcceptOnceFileListFilter()<>(
            localSourceConfig.getMaxExpectedFiles()));

        return messageSourceFactory.createFileMessageSource(
            localSourceConfig.getFolder(), Optional.of(filters));
    }
   
}
