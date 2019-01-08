package org.lrth.fsassistant.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Getter @Setter
public class VolumeConfig {
    @NotNull private String type;
    @NotNull private String path;

    /** useful for type=SFTP */
    private String host;
    @Min(1025) @Max(65536)
    private int port;
    private String user;

    // use privateKey & passphrase OR password
    private String privateKey;
    private String privateKeyPassphrase;
    private String password;

    public Optional<Resource> getPrivateKeyResource() {
        if (this.privateKey == null) {
            return Optional.empty();
        }

        // avoiding caching resource in case JMX is used in a near future
        return Optional.of(new FileSystemResource(this.privateKey));
    }
    /** ENDS useful for type=SFTP */
}
