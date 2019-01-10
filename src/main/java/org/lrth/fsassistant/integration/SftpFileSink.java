package org.lrth.fsassistant.integration;

import java.io.File;

import lombok.RequiredArgsConstructor;

import org.lrth.fsassistant.appcontext.boilerplatefactory.SftpMessageHandlerBoilerplateFactory;
import org.lrth.fsassistant.configuration.PipeConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.*;
import org.springframework.messaging.MessageHandler;

import javax.validation.constraints.NotNull;

@Configuration
@RequiredArgsConstructor
public class SftpFileSink {
	
    @NotNull private SftpMessageHandlerBoilerplateFactory factory;

    @NotNull private MyConfig config;

    @ConfigurationProperties(prefix = "fs-assistant.file-uploader")
    private static class MyConfig extends PipeConfig {}

    @Bean
    @ServiceActivator(inputChannel = "${fs-assistant.file-uploader.task.channel}")
    public MessageHandler handler() {
        return factory.create(config);
    }

    /** Alternative to inputChannel, this gateway allows other java classes
     * to upload files via POJO method call
     */
    @MessagingGateway
    public interface UploadGateway {
        @Gateway(requestChannel = "${fs-assistant.file-uploader.task.channel}")
        void upload(File file);
    }
}
