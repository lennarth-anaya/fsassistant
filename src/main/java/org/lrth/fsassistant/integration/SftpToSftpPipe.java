package org.lrth.fsassistant.integration;

import lombok.RequiredArgsConstructor;
import org.lrth.fsassistant.appcontext.boilerplatefactory.SftpMessageHandlerBoilerplateFactory;
import org.lrth.fsassistant.configuration.AppConfig;
import org.lrth.fsassistant.configuration.PipeConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.*;
import org.springframework.messaging.MessageHandler;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
@RequiredArgsConstructor
public class SftpToSftpPipe {
//    private static final String PIPE_ID = "move-from-sftp-to-sftp-pipe";
//
//    private final AppConfig appConfig;
//    private PipeConfig config;
//
//    @PostConstruct
//    public void init() {
//        this.config = appConfig.getPipes().get(PIPE_ID);
//    }
//
//    /* ****** PIPE SOURCE ******* */
//
//    // we are going to reuse source on SftpToLocalPipe pointing its channel to below one
//
//    /* ****** PIPE SINK ******* */
//
//    private final SftpMessageHandlerBoilerplateFactory factory;
//
//    @Bean
//    @ServiceActivator(inputChannel = "remoteStore")
//    public MessageHandler sftpSink() {
//        return factory.create(config.getTargetVolumeMeta());
//    }
//
//    /** Alternative to inputChannel, this gateway allows other java classes
//     * to upload files via POJO method call
//     */
//    @MessagingGateway
//    public interface UploadGateway {
//        @Gateway(requestChannel = "remoteStore")
//        void upload(File file);
//    }

}
