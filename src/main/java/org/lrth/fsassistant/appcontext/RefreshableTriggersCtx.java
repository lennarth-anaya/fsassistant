package org.lrth.fsassistant.appcontext;

import lombok.RequiredArgsConstructor;
import org.lrth.fsassistant.configuration.FileUploaderConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;

import org.lrth.fsassistant.configuration.FileDownloaderConfig;
import org.lrth.fsassistant.configuration.FolderCleanerConfig;

import javax.validation.constraints.NotNull;

@Configuration
@RequiredArgsConstructor
public class RefreshableTriggersCtx {

   @NotNull private FileUploaderConfig filesUploaderConfig;
   @NotNull private FileDownloaderConfig filesDownloaderonfig;
   @NotNull private FolderCleanerConfig folderCleanerConfig;
   
   private String prevFileTransferCronExp = null;
   private CronTrigger fileTransferCronTrigger;

   private String prevFolderCleanerCronExp = null;
   private CronTrigger folderCleanerCronTrigger;

   @Bean("filesUploaderTrigger")
   public Trigger filesUploaderTrigger() {
       return (tctx) -> {
           // this is the trick, exposing config via JMX or other mean would refresh the trigger
           final String curCronExp = this.filesUploaderConfig.getTask().getCron();

           if (!curCronExp.equals(this.prevFileTransferCronExp)) {
               this.prevFileTransferCronExp = curCronExp;
               this.fileTransferCronTrigger = new CronTrigger(curCronExp);
           }
         
           return this.fileTransferCronTrigger.nextExecutionTime(tctx);
       };
   }

   @Bean("filesDownloaderTrigger")
   public Trigger filesDownloaderTrigger() {
       return (tctx) -> {
           // this is the trick, exposing config via JMX or other mean would refresh the trigger
           final String curCronExp = this.filesDownloaderonfig.getTask().getCron();

           if (!curCronExp.equals(this.prevFileTransferCronExp)) {
               this.prevFileTransferCronExp = curCronExp;
               this.fileTransferCronTrigger = new CronTrigger(curCronExp);
           }

           return this.fileTransferCronTrigger.nextExecutionTime(tctx);
       };
   }

   @Bean("folderCleanerTrigger")
   public Trigger folderCleanerTrigger() {
   	return (tctx) -> {
         // this is the trick, exposing config via JMX or other mean would refresh the trigger
         final String curCronExp = this.folderCleanerConfig.getTask().getCron();

         if (!curCronExp.equals(this.prevFolderCleanerCronExp)) {
            this.prevFolderCleanerCronExp = curCronExp;
            this.folderCleanerCronTrigger = new CronTrigger(curCronExp);
         }
         
         return this.folderCleanerCronTrigger.nextExecutionTime(tctx);
      };
   }
   
}
