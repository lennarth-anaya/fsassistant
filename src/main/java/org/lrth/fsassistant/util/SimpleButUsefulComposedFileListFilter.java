package org.lrth.fsassistant.util;

import org.springframework.integration.file.filters.FileListFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SimpleButUsefulComposedFileListFilter implements FileListFilter<File> {
    private ArrayList<FileListFilter<File>> fileFilters;

    @Override
    public List<File> filterFiles(File[] files) {
        ArrayList<File> filteredFiles = new ArrayList<>();

        for (File file : files) {
            fileFilters.forEach(filter -> filter.filterFiles({file}));
            filteredFiles.add(file);
        }

        return filteredFiles;
    }

    public void addFilter(FileListFilter<File> fileListFilter) {
        fileFilters.add(fileListFilter);
    }
}
