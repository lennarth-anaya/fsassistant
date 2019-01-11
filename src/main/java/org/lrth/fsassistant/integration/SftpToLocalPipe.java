package org.lrth.fsassistant.integration;

import lombok.RequiredArgsConstructor;
import org.lrth.fsassistant.appcontext.boilerplatefactory.FileWritingMessageHandlerBoilerplateFactory;
import org.lrth.fsassistant.appcontext.boilerplatefactory.SftpInboundFileSynchronizingMessageSourceBoilerplateFactory;
import org.lrth.fsassistant.configuration.AppConfig;
import org.lrth.fsassistant.configuration.PipeConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.File;

@Configuration
@RequiredArgsConstructor
public class SftpToLocalPipe {

    final private AppConfig appConfig;

    final private MyPipeConfig config;

    @Component
    @ConfigurationProperties(prefix="fs-assistant.move-from-sftp-to-local-pipe")
    private static class MyPipeConfig extends PipeConfig {}

    /* ****** PIPE SOURCE ******* */

    @NotNull
    private SftpInboundFileSynchronizingMessageSourceBoilerplateFactory msgSourceFactory;

    private String cachedCronExp;
    private CronTrigger cachedCronTrigger;

    @Bean
    public Trigger sftpPipePoller() {
        return (tctx) -> {
            if (this.config == null || this.config.getTask() == null) {
                return new java.util.Date(0);
            }

            // this is the trick, exposing config via JMX or other mean would refresh the trigger
            final String curCronExp = this.config.getTask().getCron();

            if (!curCronExp.equals(this.cachedCronExp)) {
                this.cachedCronExp = curCronExp;
                this.cachedCronTrigger = new CronTrigger(curCronExp);
            }

            return this.cachedCronTrigger.nextExecutionTime(tctx);
        };
    }

    @InboundChannelAdapter(
            value = "${fs-assistant.move-from-sftp-to-local-pipe.task.channel}",
            poller = @Poller(trigger = "sftpPipePoller")
    )
    public MessageSource<File> pollFiles() {
        return msgSourceFactory.create(config.getSourceVolumeMeta());
    }

    /* ****** PIPE SINK ******* */

    @NotNull private FileWritingMessageHandlerBoilerplateFactory factory;

    @ServiceActivator(inputChannel = "${fs-assistant.move-from-sftp-to-local-pipe.task.channel}")
    public MessageHandler localPipeWriter() {
        if( config == null ) {
            return null;
        }

        return factory.create(config.getTargetVolumeMeta());
    }

}