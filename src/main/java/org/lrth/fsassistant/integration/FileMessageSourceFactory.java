package org.lrth.fsassistant.integration;

import java.io.File;
import java.util.Optional;

import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.stereotype.Component;

/**
 * @author lanaya
 *
 * This class is intended to be used by final Message Sources, not to be one.
 */
@Component
public class FileMessageSourceFactory {

   protected MessageSource<File> createFileMessageSource(
      final String folder,
      final Optional<CompositeFileListFilter<File>> optionalFilters
   ) {
   	  FileReadingMessageSource source = new FileReadingMessageSource();
   	
      source.setAutoCreateDirectory(true);
      source.setDirectory(new File(folder));
      
      optionalFilters.ifPresent(filters -> source.setFilter(filters));

      return source;
   }
   
}
