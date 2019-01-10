package org.lrth.fsassistant.configuration;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * VolumeConfig are reusable configurations that can
 * be referred by tasks when actually manipulating them.
 */
@Getter @Setter
public class VolumeConfigTaskMeta {
    @NotNull private String volumeRef;
    @NotNull private List<String> fileExtensions;
    private int maxExpectedFiles;
    private Boolean autoCreateDirectory;
    private Boolean deleteSourceFiles;

    /** Virtual attribute populated by PipeConfig based on volumeRef above */
    private VolumeConfig volumeDef;
}
