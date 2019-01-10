package org.lrth.fsassistant.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;

@Component
public class LocalFolderCleanupSink {

//    TODO: it seems next is not necessary since this is a Sink, so we just need to
//    create another LocalFolderPoller with its corresponding cron
//
//    @Bean("folderCleanerTrigger")
//    public Trigger folderCleanerTrigger() {
//        return (tctx) -> {
//            // this is the trick, exposing config via JMX or other mean would refresh the trigger
//            final String curCronExp = this.folderCleanerConfig.getTask().getCron();
//
//            if (!curCronExp.equals(this.prevFolderCleanerCronExp)) {
//                this.prevFolderCleanerCronExp = curCronExp;
//                this.folderCleanerCronTrigger = new CronTrigger(curCronExp);
//            }
//
//            return this.folderCleanerCronTrigger.nextExecutionTime(tctx);
//        };
//    }


    public MessageHandler handler() {
        return message -> {
            File file = (File) message.getPayload();
            file.delete();
        };
    }
}