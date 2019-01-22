package org.lrth.fsassistant.integration;

import lombok.RequiredArgsConstructor;
import org.lrth.fsassistant.appcontext.boilerplatefactory.FileReadingMessageSourceBoilerplateFactory;
import org.lrth.fsassistant.configuration.AppConfig;
import org.lrth.fsassistant.configuration.PipeConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
@RequiredArgsConstructor
public class LocalToSftpPipe {

    private static final String PIPE_ID = "move-from-local-to-sftp-pipe";

    private final AppConfig appConfig;
    private PipeConfig config;

    @PostConstruct
    public void init() {
        this.config = appConfig.getPipes().get(PIPE_ID);
    }

    private final FileReadingMessageSourceBoilerplateFactory factory;

    private String cachedCronExp;
    private CronTrigger cachedCronTrigger;

    @Bean
    public Trigger localFilesPollerTrigger() {
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


    // WARNING: This is not deleting source file. It could be used the processAfterCommit
    //  by adding a transaction context, or just use the cleanup pipe provided in this project.
    /**
     * We could have used UploadGateway, but using Spring Integration Channels instead.
     * @return
     */
    @Bean
    @InboundChannelAdapter(
        value = "remoteStore",
        poller = @Poller(trigger = "localFilesPollerTrigger")
    )
    public MessageSource<File> localFolderPoller() {
        if( config.getTask().isForceSuspend() ) {
            return null;
        }

        // TODO sending PIPE_ID is dirty
        return factory.create(config.getSourceVolumeMeta(), true, PIPE_ID);
    }

}
