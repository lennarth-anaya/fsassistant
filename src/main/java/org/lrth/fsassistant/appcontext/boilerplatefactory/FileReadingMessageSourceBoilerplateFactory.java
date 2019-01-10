package org.lrth.fsassistant.appcontext.boilerplatefactory;

import org.lrth.fsassistant.configuration.PipeConfig;
import org.lrth.fsassistant.configuration.VolumeConfig;
import org.lrth.fsassistant.configuration.VolumeConfigTaskMeta;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.ChainFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileReadingMessageSourceBoilerplateFactory {
    public FileReadingMessageSource create(PipeConfig config) {
        FileReadingMessageSource source;

        ChainFileListFilter<File> filters = new ChainFileListFilter<>();
        VolumeConfig volumeConfig = config.getSourceVolumeConfig();
        VolumeConfigTaskMeta taskConfig = config.getSourceVolumeMeta();

        // add all file extensions from configuration
        taskConfig.getFileExtensions().forEach(ext ->
            filters.addFilter(new SimplePatternFileListFilter(ext)));

        filters.addFilter(new AcceptOnceFileListFilter<>(
                taskConfig.getMaxExpectedFiles()));

        source = new FileReadingMessageSource();

        source.setAutoCreateDirectory(true);
        source.setDirectory(new File(volumeConfig.getPath()));

        source.setFilter(filters);

        return source;
    }
}
