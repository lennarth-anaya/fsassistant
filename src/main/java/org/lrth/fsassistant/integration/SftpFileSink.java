package org.lrth.fsassistant.integration;

import java.util.Optional;

import java.io.File;

import com.jcraft.jsch.ChannelSftp;
import lombok.RequiredArgsConstructor;

import org.lrth.fsassistant.configuration.FileUploaderConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.*;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.messaging.MessageHandler;

import javax.validation.constraints.NotNull;

@Configuration
@RequiredArgsConstructor
public class SftpFileSink {
	
    @NotNull private FileUploaderConfig config;
    @NotNull private SessionFactory<ChannelSftp.LsEntry> localFolderPollerSessionFactory;

    /** Alternative to inputChannel, this gateway allows other java classes
     * to upload files via POJO method call
     */
    @MessagingGateway
    public interface UploadGateway {
        @Gateway(requestChannel = "${fs-assistant.file-uploader.task.channel}")
        void upload(File file);

    }

    @Bean
    @ServiceActivator(inputChannel = "${fs-assistant.file-uploader.task.channel}")
    public MessageHandler handler() {
        String sftpRemoteDirectory = config.getTargetVolumeConfig().getPath();

        SftpMessageHandler handler = new SftpMessageHandler(localFolderPollerSessionFactory);
        handler.setRemoteDirectoryExpression(new LiteralExpression(sftpRemoteDirectory));
        handler.setFileNameGenerator(message -> {
            if (message.getPayload() instanceof File) {
                return ((File) message.getPayload()).getName();
            } else {
                throw new IllegalArgumentException("Payload is not a File");
            }
        });
        return handler;
    }
}
