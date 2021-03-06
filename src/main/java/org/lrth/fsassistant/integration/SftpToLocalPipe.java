package org.lrth.fsassistant.integration;

import lombok.RequiredArgsConstructor;
import org.lrth.fsassistant.appcontext.boilerplatefactory.FileWritingMessageHandlerBoilerplateFactory;
import org.lrth.fsassistant.appcontext.boilerplatefactory.SftpInboundFileSynchronizingMessageSourceBoilerplateFactory;
import org.lrth.fsassistant.configuration.AppConfig;
import org.lrth.fsassistant.configuration.PipeConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
@RequiredArgsConstructor
public class SftpToLocalPipe {

    private static final String PIPE_ID = "move-from-sftp-to-local-pipe";

    private final AppConfig appConfig;

    private PipeConfig config;

    @PostConstruct
    public void init() {
        this.config = appConfig.getPipes().get(PIPE_ID);
    }


    /* ****** PIPE SOURCE ******* */

    private final SftpInboundFileSynchronizingMessageSourceBoilerplateFactory msgSourceFactory;

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

    @Bean
    @InboundChannelAdapter(
            value = "remoteStore",
            // value = "localStore",
            poller = @Poller(trigger = "sftpPipePoller")
    )
    public MessageSource<File> pollFiles() {
        if (config.getTask().isForceSuspend()) {
            return null;
        }

        return msgSourceFactory.create(config.getSourceVolumeMeta(),
                config.getTargetVolumeMeta());
    }

    /* ****** PIPE SINK ******* */

    private final FileWritingMessageHandlerBoilerplateFactory factory;

    @Bean
//    @ServiceActivator(inputChannel = "localStore")
    public MessageHandler localPipeWriter() {
        if (config.getTask().isForceSuspend()) {
            return null;
        }

        return factory.create(config.getTargetVolumeMeta());
    }

}
