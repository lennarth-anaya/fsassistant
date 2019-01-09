package org.lrth.fsassistant.appcontext;

import com.jcraft.jsch.ChannelSftp;
import lombok.RequiredArgsConstructor;

import org.lrth.fsassistant.appcontext.factory.SftpInbFilesSyncBoilerplateFactory;
import org.lrth.fsassistant.appcontext.factory.SftpSessionBoilerplateFactory;
import org.lrth.fsassistant.configuration.FileDownloaderConfig;
import org.lrth.fsassistant.configuration.FileUploaderConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;

import javax.validation.constraints.NotNull;

@Configuration
@RequiredArgsConstructor
public class FilesDownloaderContext {

    @NotNull private FileDownloaderConfig downloaderConfig;
    @NotNull private FileUploaderConfig uploaderConfig;
    @NotNull private SftpSessionBoilerplateFactory sftpSessionBoilerplateFactory;
    @NotNull private SftpInbFilesSyncBoilerplateFactory uploaderSftpSyncFactory;

    @NotNull private SessionFactory<ChannelSftp.LsEntry> downloaderSftpSessionFactory;
    @NotNull private SessionFactory<ChannelSftp.LsEntry> localFolderPollerSessionFactory;

    @Bean
    public SessionFactory<ChannelSftp.LsEntry> downloaderSftpSessionFactory() {
        return sftpSessionBoilerplateFactory.create(downloaderConfig.getSourceVolumeConfig());
    }

    @Bean
    public SessionFactory<ChannelSftp.LsEntry> localFolderPollerSessionFactory() {
        return sftpSessionBoilerplateFactory.create(uploaderConfig.getSourceVolumeConfig());
    }

    @Bean
    public SftpInboundFileSynchronizer downloaderSftpFileSynchronizer() {
        return uploaderSftpSyncFactory.create(
            downloaderSftpSessionFactory, downloaderConfig.getSourceVolumeConfig(), downloaderConfig.getSourceVolumeMeta());
    }

    @Bean
    public SftpInboundFileSynchronizer localFolderSynchronizer() {
        return uploaderSftpSyncFactory.create(
                localFolderPollerSessionFactory, downloaderConfig.getSourceVolumeConfig(), downloaderConfig.getSourceVolumeMeta());
    }

}
