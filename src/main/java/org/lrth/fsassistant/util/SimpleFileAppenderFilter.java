package org.lrth.fsassistant.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.integration.file.filters.FileListFilter;

/**
 * @author Lennarth Anaya
 * 
 * This filter allows to append additional files 
 * to the chain of filtered files.<br />
 * <br />
 * Combined with DiscardAwareFileListFilter implementations,
 * certain files that shouldn't have been excluded can be
 * reincluded in the filtered chain.
 * .
 */
public class SimpleFileAppenderFilter
    implements FileListFilter<File>
{
    private ArrayList<File> filesToBeIncluded = new ArrayList<>();
    
    @Override    
    public List<File> filterFiles(File[] files) {
        if (files == null) {
            return filesToBeIncluded;
        }
        
        // Arrays.asList is returning an immutable arraylist in Java 8
        List<File> all = new ArrayList<>();
        
        // mix both lists
        all.addAll(Arrays.asList(files));
        all.addAll(filesToBeIncluded);
        
        return all;
    }

    public void addFileForInclusion(File file) {
        filesToBeIncluded.add(file);
    }
}
