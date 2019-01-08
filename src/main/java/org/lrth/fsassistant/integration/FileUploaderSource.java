package org.lrth.fsassistant.integration;

import java.util.Optional;

import java.io.File;

import lombok.RequiredArgsConstructor;

import org.lrth.fsassistant.configuration.FileDownloaderConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.*;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.filters.ChainFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.sftp.filters.SftpPersistentAcceptOnceFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.messaging.MessageHandler;

import javax.validation.constraints.NotNull;

@Configuration
@RequiredArgsConstructor
public class FileUploaderSource {
	
    @NotNull private FileMessageSourceFactory messageSourceFactory;
    @NotNull private FileDownloaderConfig config;
    @NotNull private SftpInboundFileSynchronizer sftpInboundFileSynchronizer;
    @NotNull private UploadGateway uploader;

    @Bean
    @InboundChannelAdapter(
        value = "${fs-assistant.file-uploader.channel}",
        poller = @Poller(trigger = "filesUploaderTrigger")
    )
    public MessageSource<File> fileReadingMessageSource() {
//        ChainFileListFilter<File> filters = new ChainFileListFilter<>();
//
//        // add all file extensions from configuration
//        localSourceConfig.getFileExtensions().forEach(ext -> filters.addFilter(
//            new SimplePatternFileListFilter(ext)));
//
//        filters.addFilter(new SftpPersistentAcceptOnceFileListFilter()<>(
//            localSourceConfig.getMaxExpectedFiles()));
//
//        return messageSourceFactory.createFileMessageSource(
//            localSourceConfig.getFolder(), Optional.of(filters));
    }

    /** Allows other java classes to also upload files programmatically */
    @MessagingGateway
    public interface UploadGateway {
        @Gateway(requestChannel = "${fs-assistant.file-uploader.channel}")
        void upload(File file);

    }

    @Bean
    @ServiceActivator(inputChannel = "toSftpChannel")
    public MessageHandler handler() {
        SftpMessageHandler handler = new SftpMessageHandler();
        handler.setRemoteDirectoryExpression(new LiteralExpression(sftpRemoteDirectory));
        handler.setFileNameGenerator(new FileNameGenerator() {
            @Override
            public String generateFileName(Message<?> message) {
                if (message.getPayload() instanceof File) {
                    return ((File) message.getPayload()).getName();
                } else {
                    throw new IllegalArgumentException("File expected as payload.");
                }
            }
        });
        return handler;
    }
}
