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
public class VolumeConfigRef {
    @NotNull private String volumeRef;
    @NotNull private List<String> fileExtensions;

    /** Useful for tasks dealing with SFTP volumes */
    private Boolean deleteRemoteFiles;
}
