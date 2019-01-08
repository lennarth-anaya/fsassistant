package org.lrth.fsassistant.integration;

import lombok.RequiredArgsConstructor;
import org.lrth.fsassistant.configuration.FileDownloaderConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.annotation.*;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.messaging.MessageHandler;

import javax.validation.constraints.NotNull;
import java.io.File;

@RequiredArgsConstructor
public class FileDownloader {

    @NotNull private SftpInboundFileSynchronizer sftpInboundFileSynchronizer;
    @NotNull private FileDownloaderConfig config;

    @Bean
    @InboundChannelAdapter(
            value = "${fs-assistant.file-downloader.channel}",
            poller = @Poller(trigger = "filesDownloaderTrigger")
    )
    public MessageSource<File> downloadFiles() {
        SftpInboundFileSynchronizingMessageSource source =
            new SftpInboundFileSynchronizingMessageSource(sftpInboundFileSynchronizer);

        source.setLocalDirectory(new File(config.getTargetVolumeConfig().getPath()));
        source.setAutoCreateLocalDirectory(
                config.getTargetVolumeMeta().getAutoCreateDirectory());
        source.setLocalFilter(new AcceptOnceFileListFilter<>());

        return source;
    }

    @Bean
    @ServiceActivator(inputChannel = "${fs-assistant.file-downloader.channel}")
    public MessageHandler storeDownloadedFile() {
        String localFolderToDownload = config.getTargetVolumeConfig().getPath();

        Expression directoryExpression = new SpelExpressionParser().parseExpression(localFolderToDownload);
        FileWritingMessageHandler fileWriterHandler = new FileWritingMessageHandler(directoryExpression);

        fileWriterHandler.setFileExistsMode(FileExistsMode.IGNORE);
        fileWriterHandler.setDeleteSourceFiles(config.getSourceVolumeMeta().getDeleteSourceFiles());
        fileWriterHandler.setAutoCreateDirectory(config.getTargetVolumeMeta().getAutoCreateDirectory());

        return fileWriterHandler;
    }

}
