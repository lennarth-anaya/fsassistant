package org.lrth.fsassistant.integration;

import lombok.RequiredArgsConstructor;
import org.lrth.fsassistant.appcontext.boilerplatefactory.SftpInboundFileSynchronizingMessageSourceBoilerplateFactory;
import org.lrth.fsassistant.configuration.PipeConfig;
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

@Configuration
@RequiredArgsConstructor
public class SftpFolderPoller {

//    @NotNull
//    private SftpInboundFileSynchronizingMessageSourceBoilerplateFactory msgSourceFactory;
//
//    @NotNull
//    private MyPipeConfig config;
//
//    @Component
//    @ConfigurationProperties(prefix="fs-assistant.file-downloder")
//    private static class MyPipeConfig extends PipeConfig {}
//
//    private String cachedCronExp;
//    private CronTrigger cachedCronTrigger;
//
//    @Bean
//    public Trigger filesDownloaderTrigger() {
//        return (tctx) -> {
//            if (this.config == null) {
//                return new java.util.Date(0);
//            }
//
//            // this is the trick, exposing config via JMX or other mean would refresh the trigger
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
//    @InboundChannelAdapter(
//        value = "${fs-assistant.file-downloader.task.channel}",
//        poller = @Poller(trigger = "filesDownloaderTrigger")
//    )
//    public MessageSource<File> sftpFolderPoller() {
//        if(config == null) {
//            return null;
//        }
//
//        return msgSourceFactory.create(config.getSourceVolumeMeta());
//    }
}
