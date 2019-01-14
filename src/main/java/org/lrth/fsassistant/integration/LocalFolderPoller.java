package org.lrth.fsassistant.integration;

import lombok.RequiredArgsConstructor;
import org.lrth.fsassistant.appcontext.boilerplatefactory.FileReadingMessageSourceBoilerplateFactory;
import org.lrth.fsassistant.configuration.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;

import javax.validation.constraints.NotNull;
import java.io.File;

// @Configuration
@RequiredArgsConstructor
public class LocalFolderPoller {

//    @NotNull private FileReadingMessageSourceBoilerplateFactory factory;
//    @NotNull private MyPipeConfig config;
//
//    @Component
//    @ConfigurationProperties(prefix = "fs-assistant.file-uploader")
//    private static class MyPipeConfig extends PipeConfig {}
//
//    private String cachedCronExp;
//    private CronTrigger cachedCronTrigger;
//
//    @Bean
//    public Trigger filesUploaderTrigger() {
//        return (tctx) -> {
//            if (this.config == null) {
//                return new java.util.Date(0);
//            }
//
//            // this is the trick for live-editable cron expression:
//            //    exposing config via JMX or other mean would refresh the trigger
//            final String curCronExp = this.config.getTask().getCron();
//
//            if (!curCronExp.equals(this.cachedCronExp)) {
//                this.cachedCronExp = curCronExp;
//                this.cachedCronTrigger = new CronTrigger(curCronExp);
//            }
//
//            return this.cachedCronTrigger.nextExecutionTime(tctx);
//        };
//    }
//
//    /**
//     * We could have used UploadGateway, but using Spring Integration Channels instead.
//     * @return
//     */
//    @InboundChannelAdapter(
//        value = "${fs-assistant.file-uploader.task.channel}",
//        poller = @Poller(trigger = "filesUploaderTrigger")
//    )
//    public MessageSource<File> localFolderPoller() {
//        if( config == null ) {
//            return null;
//        }
//
//        return factory.create(config.getSourceVolumeMeta());
//    }

}
