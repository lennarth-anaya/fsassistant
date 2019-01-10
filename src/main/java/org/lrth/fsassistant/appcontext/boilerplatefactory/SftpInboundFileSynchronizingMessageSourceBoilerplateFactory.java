package org.lrth.fsassistant.appcontext.boilerplatefactory;

import com.jcraft.jsch.ChannelSftp;
import org.lrth.fsassistant.configuration.PipeConfig;
import org.lrth.fsassistant.configuration.VolumeConfig;
import org.lrth.fsassistant.configuration.VolumeConfigTaskMeta;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.File;

@Component
public class SftpInboundFileSynchronizingMessageSourceBoilerplateFactory {

    @NotNull
    private SftpInbFilesSyncBoilerplateFactory sftpSyncFactory;

    @NotNull
    private SftpSessionBoilerplateFactory sftpSessionBoilerplateFactory;

    public MessageSource<File> create(VolumeConfigTaskMeta volumeTaskMeta) {
        SftpInboundFileSynchronizingMessageSource source;

        VolumeConfig volumeConfig = volumeTaskMeta.getVolumeDef();

        SessionFactory<ChannelSftp.LsEntry> sessionFactory = sftpSessionBoilerplateFactory
                .create(volumeConfig);

        SftpInboundFileSynchronizer sftpFileSynchronizer = sftpSyncFactory.create(
                sessionFactory, volumeConfig, volumeTaskMeta);

        source = new SftpInboundFileSynchronizingMessageSource(sftpFileSynchronizer);

        source.setLocalDirectory(new File(volumeConfig.getPath()));
        source.setAutoCreateLocalDirectory(volumeTaskMeta.getAutoCreateDirectory());
        source.setLocalFilter(new AcceptOnceFileListFilter<>());

        return source;
    }
}
