package org.lrth.fsassistant.appcontext;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrationFlow {
   @Bean
   public IntegrationFlow processFileFlow() {
      return IntegrationFlows
         .from("input")
         .transform(fileToStringTransformer())
         .handle("fileProcessor", "process").get();
   }
}