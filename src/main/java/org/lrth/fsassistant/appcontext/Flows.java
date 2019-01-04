package org.lrth.fsassistant.appcontext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

@Configuration
public class Flows {

    @Value("${fs-assistant.file-transfer.inputChannelId}")
    private String fileTransferChannelId;

    @Value("${fs-assistant.folder-cleaner.inputChannelId}")
    private String folderCleanerChannelId;
    
    @Bean
    public IntegrationFlow fileTransferringFlow() {
        return IntegrationFlows
            .from(fileTransferChannelId)
            .transform(fileToStringTransformer())
            .get();
    }

    @Bean
    public IntegrationFlow folderCleanupFlow() {
        return IntegrationFlows
            .from(folderCleanerChannelId)
            .transform(fileToStringTransformer())
            .get();
    }

}