package org.lrth.fsassistant.configuration;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class VolumeConfigTaskMetaFileFilters {
    private List<String> fileExtensions;
    private boolean filterOutFolders;
    private long fileModificationMinAgeSeconds;
}
