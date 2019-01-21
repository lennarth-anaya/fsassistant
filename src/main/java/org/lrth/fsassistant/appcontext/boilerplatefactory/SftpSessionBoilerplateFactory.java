package org.lrth.fsassistant.appcontext.boilerplatefactory;

import com.jcraft.jsch.ChannelSftp;
import org.lrth.fsassistant.configuration.VolumeConfig;
import org.springframework.core.io.Resource;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SftpSessionBoilerplateFactory {

    public SessionFactory<ChannelSftp.LsEntry> create(VolumeConfig volumeConfig) {
        DefaultSftpSessionFactory sessFactory = new DefaultSftpSessionFactory();

        sessFactory.setAllowUnknownKeys(true);
        // you can use next instead of allowingUnkownKeys, safer
        // sessFactory.setKnownHosts("known_hosts file name");

        sessFactory.setHost(volumeConfig.getHost());
        sessFactory.setPort(volumeConfig.getPort());
        sessFactory.setUser(volumeConfig.getUser());

        Optional<Resource> privateKeyResource = volumeConfig.getPrivateKeyResource();

        privateKeyResource.ifPresent(r -> {
            sessFactory.setPrivateKey(r);
            sessFactory.setPrivateKeyPassphrase(volumeConfig.getPrivateKeyPassphrase());
        });

        // TODO with Java 9+ a neater ifPresentOrElse is available
        if (!privateKeyResource.isPresent()) {
            sessFactory.setPassword(volumeConfig.getPassword());
        }

        return sessFactory;
    }

}
