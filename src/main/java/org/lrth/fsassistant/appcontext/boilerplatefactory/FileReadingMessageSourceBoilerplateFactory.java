package org.lrth.fsassistant.appcontext.boilerplatefactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.lrth.fsassistant.configuration.VolumeConfig;
import org.lrth.fsassistant.configuration.VolumeConfigTaskMeta;
import org.lrth.fsassistant.configuration.VolumeConfigTaskMetaFileFilters;
import org.lrth.fsassistant.util.SimpleFileAppenderFilter;
import org.lrth.fsassistant.util.SimplePatternFilesListFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.RecursiveDirectoryScanner;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.ChainFileListFilter;
import org.springframework.integration.file.filters.LastModifiedFileListFilter;
import org.springframework.stereotype.Component;

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
            LastModifiedFileListFilter lastModifFilter = new LastModifiedFileListFilter(
                    fileFiltersConfig.getFileModificationMinAgeSeconds());

            // Fix: include empty folders removal sooner regardless modification time
            // which is changed by its inner files removal
            SimpleFileAppenderFilter emptyFoldersAppenderFilter = new SimpleFileAppenderFilter();
//            lastModifFilter.addDiscardCallback(file -> {
//                if (file.isDirectory() && file.list().length == 0) {
//                    emptyFoldersAppenderFilter.addFileForInclusion(file);
//                }
//            });
            // callback above is lost by Spring, implementing custom filter as a less efficient workaround of the workaround
            filters.addFilter(files -> {
                for (File file : files) {
                    if (file.isDirectory() && file.list().length == 0) {
                        emptyFoldersAppenderFilter.addFileForInclusion(file);
                    }
                }

                // no real filter at all, retrieve every file
                return Arrays.asList(files);
            });

            filters.addFilter(lastModifFilter);
            filters.addFilter(emptyFoldersAppenderFilter);
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
