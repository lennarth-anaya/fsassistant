package org.lrth.fsassistant.appcontext;

import com.jcraft.jsch.ChannelSftp;
import lombok.RequiredArgsConstructor;

import org.lrth.fsassistant.appcontext.factory.SftpInbFilesSyncBoilerplateFactory;
import org.lrth.fsassistant.appcontext.factory.SftpSessionBoilerplateFactory;
import org.lrth.fsassistant.configuration.FileDownloaderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;

import javax.validation.constraints.NotNull;

@Configuration
@RequiredArgsConstructor
public class FilesUploaderCtx {

    @NotNull private FileDownloaderConfig config;
    @NotNull private SftpSessionBoilerplateFactory sftpSessionBoilerplateFactory;
    @NotNull private SftpInbFilesSyncBoilerplateFactory uploaderSftpSyncFactory;

    @Qualifier("uploaderSftpSessionFactory") @NotNull
    private SessionFactory<ChannelSftp.LsEntry> uploaderSftpSessionFactory;

    @Bean("uploaderSftpSessionFactory")
    public SessionFactory<ChannelSftp.LsEntry> uploaderSftpSessionFactory() {
        return sftpSessionBoilerplateFactory.create(config.getSourceVolumeConfig());
    }

    @Bean("downloaderSftpSessionFactory")
    public SessionFactory<ChannelSftp.LsEntry> uploaderSftpSessionFactory() {
        return sftpSessionBoilerplateFactory.create(config.getSourceVolumeConfig());
    }

    @Bean
    public SftpInboundFileSynchronizer uploaderSftpFileSynchronizer() {
        return uploaderSftpSyncFactory.create(
            uploaderSftpSessionFactory, config.getSourceVolumeConfig(), config.getSourceVolumeMeta());
    }

}
