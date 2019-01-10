package org.lrth.fsassistant.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;

import java.io.File;

@Configuration
public class LocalFolderCleanupSink {

    @Bean
    @ServiceActivator(inputChannel = "${fs-assistant.file-downloader.task.channel}")
    public MessageHandler handler() {
        return message -> {
            File file = (File) message.getPayload();
            file.delete();
        };
    }
}