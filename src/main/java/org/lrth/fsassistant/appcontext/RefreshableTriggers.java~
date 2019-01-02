package org.lrth.fsassistant.appcontext;

import java.util.Date;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;

import org.lrth.fsassistant.configuration.FileTransferConfig;
import org.lrth.fsassistant.configuration.FolderCleanerConfig;

@Configuration
public class RefreshableTriggers {

   private FileTransferConfig fileTransferConfig;
   private FolderCleanerConfig folderCleanerConfig;
   
   private String prevFileTransferCronExp = null;
   private CronTrigger fileTransferCronTrigger;

   private String prevFileCleanerCronExp = null;
   private CronTrigger folderCleanerCronTrigger;

   public RefreshableTriggers(
      FileTransferConfig fileTransferConfig,
      FolderCleanerConfig folderCleanerCronTrigger
   ) {
      this.fileTransferConfig = fileTransferConfig;
      this.folderCleanerCronTrigger = folderCleanerCronTrigger;
   }
   
   @Bean("fileTransferTrigger")
   public Trigger fileTransferTrigger() {
   	return (tctx) -> {
         // this is the trick, exposing config via JMX or other mean would refresh the trigger
         final String curCronExp = this.fileTransferConfig.getCron();

         if (!curCronExp.equals(this.prevFileTransferCronExp)) {
            this.prevFileTransferCronExp = curCronExp;
            this.fileTransferCronTrigger = new CronTrigger(curCronExp);
         }
         
         return this.fileTransferCronTrigger;
      };
   }

   @Bean("folderCleanerTrigger")
   public Trigger folderCleanerTrigger() {
   	return (tctx) -> {
         // this is the trick, exposing config via JMX or other mean would refresh the trigger
         final String curCronExp = this.folderCleanerConfig.getCron();

         if (!curCronExp.equals(this.prevFolderCleanerCronExp)) {
            this.prevFolderCleanerCronExp = curCronExp;
            this.folderCleanerCronTrigger = new CronTrigger(curCronExp);
         }
         
         return this.folderCleanerCronTrigger;
      };
   }
   
}
