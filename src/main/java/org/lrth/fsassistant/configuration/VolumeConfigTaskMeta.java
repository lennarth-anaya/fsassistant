package org.lrth.fsassistant.configuration;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * VolumeConfig are reusable configurations that can
 * be referred by tasks when actually manipulating them.
 */
@Getter @Setter
public class VolumeConfigTaskMeta {
    @NotNull private String volumeRef;
    private int maxExpectedFiles;
    private boolean autoCreateDirectory;
    private boolean deleteSourceFiles;

    private VolumeConfigTaskMetaFileFilters filter;

    /** Virtual attribute populated by PipeConfig based on volumeRef above */
    private VolumeConfig volumeDef;
}
