package org.lrth.fsassistant.appcontext.boilerplatefactory;

import org.lrth.fsassistant.configuration.VolumeConfigTaskMetaFileFilters;
import org.lrth.fsassistant.util.SimplePatternFilesListFilter;
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

@Component
public class FileReadingMessageSourceBoilerplateFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileReadingMessageSourceBoilerplateFactory.class);

    /** @param configId is used only for debugging purposes. If there's any failure and there are several components,
     *                   this id would help find out which is the one missing any setting.
     */
    public FileReadingMessageSource create(VolumeConfigTaskMeta taskConfig, boolean pickFilesOnlyOnce, String configId) {
        FileReadingMessageSource source;

        VolumeConfigTaskMetaFileFilters fileFiltersConfig = taskConfig.getFilter();

        validate(taskConfig, configId);

        VolumeConfig volumeConfig = taskConfig.getVolumeDef();

        ChainFileListFilter<File> filters = new ChainFileListFilter<>();

        if (pickFilesOnlyOnce) {
            filters.addFilter(new AcceptOnceFileListFilter<>(taskConfig.getMaxExpectedFiles()));
        }

        if (fileFiltersConfig.getFileModificationMinAgeSeconds() > 0) {
            filters.addFilter(new LastModifiedFileListFilter(fileFiltersConfig.getFileModificationMinAgeSeconds()));
        }

        filters.addFilter(new SimplePatternFilesListFilter(
            fileFiltersConfig.getFileExtensions(), fileFiltersConfig.isFilterOutFolders()));

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

        if (taskConfig.getFilter() == null || taskConfig.getFilter().getFileExtensions() == null) {
            LOGGER.warn("{}: filter.files-extensions was undefined, hence all files will be processed", configId);
            taskConfig.getFilter().setFileExtensions(new ArrayList<>());
        }

        if (taskConfig.getMaxExpectedFiles() < 1) {
            errMsgs.append("max-expected-files must be specified and greater than zero\n");
        }

        if (errMsgs.length() > 0) {
            throw new IllegalArgumentException(errMsgs.toString());
        }
    }
}
