package org.lrth.fsassistant.integration;

import lombok.RequiredArgsConstructor;
import org.lrth.fsassistant.configuration.FileDownloaderConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.annotation.*;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.MessageHandler;

import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
public class LocalFileSink {

    @NotNull private FileDownloaderConfig config;

    @Bean
    @ServiceActivator(inputChannel = "${fs-assistant.file-downloader.task.channel}")
    public MessageHandler writeFile() {
        String localFolderToDownload = config.getTargetVolumeConfig().getPath();

        Expression directoryExpression = new SpelExpressionParser().parseExpression(localFolderToDownload);
        FileWritingMessageHandler fileWriterHandler = new FileWritingMessageHandler(directoryExpression);

        fileWriterHandler.setFileExistsMode(FileExistsMode.IGNORE);
        fileWriterHandler.setDeleteSourceFiles(config.getSourceVolumeMeta().getDeleteSourceFiles());
        fileWriterHandler.setAutoCreateDirectory(config.getTargetVolumeMeta().getAutoCreateDirectory());

        return fileWriterHandler;
    }

}
