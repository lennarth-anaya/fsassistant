package org.lrth.fsassistant.appcontext.boilerplatefactory;

import com.jcraft.jsch.ChannelSftp;
import org.lrth.fsassistant.configuration.PipeConfig;
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

    public MessageSource<File> create(PipeConfig config) {
        SftpInboundFileSynchronizingMessageSource source;

        SessionFactory<ChannelSftp.LsEntry> sessionFactory = sftpSessionBoilerplateFactory
                .create(config.getSourceVolumeConfig());

        SftpInboundFileSynchronizer sftpFileSynchronizer = sftpSyncFactory.create(
                sessionFactory, config.getSourceVolumeConfig(), config.getSourceVolumeMeta());

        source = new SftpInboundFileSynchronizingMessageSource(sftpFileSynchronizer);

        source.setLocalDirectory(new File(config.getTargetVolumeConfig().getPath()));
        source.setAutoCreateLocalDirectory(
                config.getTargetVolumeMeta().getAutoCreateDirectory());
        source.setLocalFilter(new AcceptOnceFileListFilter<>());

        return source;
    }
}
