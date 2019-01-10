package org.lrth.fsassistant.integration;

import lombok.RequiredArgsConstructor;
import org.lrth.fsassistant.appcontext.boilerplatefactory.FileWritingMessageHandlerBoilerplateFactory;
import org.lrth.fsassistant.configuration.PipeConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.*;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.File;

@Component
@RequiredArgsConstructor
public class LocalFileSink {

    @NotNull private FileWritingMessageHandlerBoilerplateFactory factory;
    @NotNull private MyPipeConfig config;

    @ConfigurationProperties(prefix="fs-assistant.file-downloder")
    private static class MyPipeConfig extends PipeConfig {}

    @Bean
    @ServiceActivator(inputChannel = "${fs-assistant.file-downloader.task.channel}")
    public MessageHandler writeFile() {
        return factory.create(config);
    }

    /** Alternative to inputChannel, this gateway allows other java classes
     * to write files via POJO method call
     */
    @MessagingGateway
    public interface WriteLocalFileGateway {
        @Gateway(requestChannel = "${fs-assistant.file-downloader.task.channel}")
        void write(File file);
    }

}
