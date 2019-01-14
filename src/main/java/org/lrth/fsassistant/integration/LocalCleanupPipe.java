package org.lrth.fsassistant.integration;

import org.lrth.fsassistant.appcontext.boilerplatefactory.FileReadingMessageSourceBoilerplateFactory;
import org.lrth.fsassistant.configuration.PipeConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.integration.core.MessageSource;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.stereotype.Component;

import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;

import lombok.RequiredArgsConstructor;

import java.io.File;

@Configuration
@RequiredArgsConstructor
public class LocalCleanupPipe {
    final private SpecificPipeConfig config;
    final private FileReadingMessageSourceBoilerplateFactory factory;
    
    private static final String PIPE_ID = "local-cleanup-pipe";
    
    @Component
    @ConfigurationProperties(prefix="fs-assistant." + PIPE_ID)
    private static class SpecificPipeConfig extends PipeConfig {}

    /* ****** PIPE SOURCE ******* */
    
    private String cachedCronExp;
    private CronTrigger cachedCronTrigger;
    
    @Bean
    public Trigger localFolderCleanupTrigger() {
        return (tctx) -> {
            if (this.config == null) {
                return new java.util.Date(0);
            }
    
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
    
    @InboundChannelAdapter(
      value = "${fs-assistant." + PIPE_ID + ".task.channel}",
      poller = @Poller(trigger = "localFolderCleanupTrigger")
    )
    public MessageSource<File> localFolderPoller() {
      if( config == null ) {
          return null;
      }
    
      return factory.create(config.getSourceVolumeMeta());
    }

    /* ****** PIPE SINK ******* */
    @ServiceActivator(inputChannel = "${fs-assistant." + PIPE_ID + ".task.channel}")
    public MessageHandler folderCleaner() {
        return message -> {
            File file = (File) message.getPayload();
            file.delete();
        };
    }

}
