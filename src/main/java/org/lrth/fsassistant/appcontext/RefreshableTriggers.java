package org.lrth.fsassistant.appcontext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;

import org.lrth.fsassistant.configuration.FileTransferConfig;
import org.lrth.fsassistant.configuration.FolderCleanerConfig;

@Configuration
public class RefreshableTriggers {

   private FileTransferConfig fileTransferConfig;
   private FolderCleanerConfig folderCleanerConfig;
   
   private String prevFileTransferCronExp = null;
   private CronTrigger fileTransferCronTrigger;

   private String prevFolderCleanerCronExp = null;
   private CronTrigger folderCleanerCronTrigger;

   public RefreshableTriggers(
      FileTransferConfig fileTransferConfig,
      FolderCleanerConfig folderCleanerConfig
   ) {
      this.fileTransferConfig = fileTransferConfig;
      this.folderCleanerConfig = folderCleanerConfig;
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
         
           return this.fileTransferCronTrigger.nextExecutionTime(tctx);
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
         
         return this.folderCleanerCronTrigger.nextExecutionTime(tctx);
      };
   }
   
}
