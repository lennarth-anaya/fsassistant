package org.lrth.fsassistant.integration;

import org.lrth.fsassistant.appcontext.boilerplatefactory.FileReadingMessageSourceBoilerplateFactory;
import org.lrth.fsassistant.configuration.AppConfig;
import org.lrth.fsassistant.configuration.PipeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.core.MessageSource;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;

import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;

import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
@RequiredArgsConstructor
public class LocalCleanupPipe {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalCleanupPipe.class);

    private final AppConfig appConfig;
    private final FileReadingMessageSourceBoilerplateFactory factory;
    
    private static final String PIPE_ID = "local-cleanup-pipe";

    private PipeConfig config;

    @PostConstruct
    public void init() {
        this.config = appConfig.getPipes().get(PIPE_ID);
    }

    /* ****** PIPE SOURCE ******* */
    
    private String cachedCronExp;
    private CronTrigger cachedCronTrigger;
    
    @Bean
    public Trigger localFolderCleanupTrigger() {
        return (tctx) -> {
            // this is the trick for live-editable cron expression:
            //    exposing config via JMX or other mean would refresh the trigger
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
        channel = "fileToRemove",
        poller = @Poller(
            trigger = "localFolderCleanupTrigger",
            maxMessagesPerPoll = "${fs-assistant.pipes.local-cleanup-pipe.task.max-messages-per-poll}"
        )
    )
    public MessageSource<File> localFolderPollerForCleanup() {
        // we want to pick folders again once they're empty
        final boolean pickFilesOnlyOnce = false;

        // TODO there should be a smarter way of debugging configId
        final String configId = PIPE_ID + ".source-volume-meta";

        return factory.create(config.getSourceVolumeMeta(), pickFilesOnlyOnce, configId);
    }

    /* ****** PIPE SINK ******* */
    @Bean
    @ServiceActivator(
        inputChannel = "fileToRemove"
    )
    public MessageHandler folderCleaner() {
        return message -> {
            File file = (File) message.getPayload();

            if (file.isDirectory() && file.list().length == 0) {
                // cleaning up empty folders too, no config parameter that would complicate VolumeConfigTaskMeta structure
                deleteFile(file);
            }

            if (file.isFile()) {
                deleteFile(file);
            }
        };
    }

    public void deleteFile(File file) {
        if ( config.getTask().isSimulationMode() ) {
            LOGGER.info("SIMULATION MODE: I would have deleted file: [{}]", file);
        } else {
            LOGGER.info("DELETING: [{}]", file);
            file.delete();
        }
    }
}
