package org.lrth.fsassistant.appcontext.boilerplatefactory;

import org.lrth.fsassistant.util.SimpleButUsefulComposedFileListFilter;
import org.slf4j.Logger;
import org.lrth.fsassistant.configuration.VolumeConfig;
import org.lrth.fsassistant.configuration.VolumeConfigTaskMeta;
import org.slf4j.LoggerFactory;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.RecursiveDirectoryScanner;
import org.springframework.integration.file.filters.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileReadingMessageSourceBoilerplateFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileReadingMessageSourceBoilerplateFactory.class);

    /** @param configId is used only for logging purposes */
    public FileReadingMessageSource create(VolumeConfigTaskMeta taskConfig, String configId) {
        FileReadingMessageSource source;

        validate(taskConfig, configId);

        //ChainFileListFilter<File> filters = new ChainFileListFilter<>();
        //CompositeFileListFilter<File> filters = new ChainFileListFilter<>();
        SimpleButUsefulComposedFileListFilter filters = new SimpleButUsefulComposedFileListFilter();

        VolumeConfig volumeConfig = taskConfig.getVolumeDef();

        filters.addFilter(new AcceptOnceFileListFilter<>(
                taskConfig.getMaxExpectedFiles()));

        // add all file extensions from configuration
        taskConfig.getFileExtensions().forEach(ext ->
            filters.addFilter(new SimplePatternFileListFilter(ext)));

        source = new FileReadingMessageSource();

        source.setAutoCreateDirectory(true);
        source.setDirectory(new File(volumeConfig.getPath()));

        RecursiveDirectoryScanner scanner = new RecursiveDirectoryScanner();
        scanner.setFilter(filters);
        source.setScanner(scanner);

        return source;
    }

    private void validate(VolumeConfigTaskMeta taskConfig, String configId) {
        StringBuilder errMsgs = new StringBuilder();

        if (taskConfig.getVolumeDef() == null) {
            errMsgs.append("missing or invalid mandatory parameter ")
                    .append(configId)
                    .append(".volume-ref\n");
        }

        if ( taskConfig.getFileExtensions() == null ) {
            LOGGER.warn(configId + ": files-extensions was undefined, hence all files will be processed");
            taskConfig.setFileExtensions(new ArrayList<>());
        }

        if (taskConfig.getMaxExpectedFiles() < 1) {
            errMsgs.append("max-expected-files must be specified and greater than zero\n");
        }

        if (errMsgs.length() > 0) {
            throw new IllegalArgumentException(errMsgs.toString());
        }
    }
}
