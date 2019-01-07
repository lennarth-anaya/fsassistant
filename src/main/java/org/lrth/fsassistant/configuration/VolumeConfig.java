package org.lrth.fsassistant.configuration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class VolumeConfig {
    @NotNull private String type;
    @NotNull private String path;

    /** Only for type=SFTP */
    private String host;
    private String privateKeyPassphrase;
    @Min(1025) @Max(65536)
    private int port;
    private String user;
    /** ENDS Only for type=SFTP */
}
