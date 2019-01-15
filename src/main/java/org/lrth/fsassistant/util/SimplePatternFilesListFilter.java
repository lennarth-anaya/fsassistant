package org.lrth.fsassistant.util;

import org.springframework.integration.file.filters.FileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SimplePatternFilesListFilter implements FileListFilter<File> {
    private ArrayList<SimplePatternFileListFilter> fileFilters = new ArrayList<>();

    public SimplePatternFilesListFilter(List<String> filteringPattern, boolean skipFolders) {
        filteringPattern.forEach(ext -> {
            SimplePatternFileListFilter filter = new SimplePatternFileListFilter(ext);
            filter.setAlwaysAcceptDirectories(!skipFolders);
            fileFilters.add(filter);
        });
    }

    @Override
    public List<File> filterFiles(File[] files) {
        HashSet<File> filteredFiles = new HashSet<>();
        fileFilters.forEach(filter -> filteredFiles.addAll(filter.filterFiles(files)));
        return new ArrayList<>(filteredFiles);
    }
}
