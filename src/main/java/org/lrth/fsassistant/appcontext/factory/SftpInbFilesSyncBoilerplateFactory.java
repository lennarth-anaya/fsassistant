package org.lrth.fsassistant.appcontext.factory;

import com.jcraft.jsch.ChannelSftp;
import org.lrth.fsassistant.configuration.VolumeConfig;
import org.lrth.fsassistant.configuration.VolumeConfigTaskMeta;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.filters.SftpSimplePatternFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.stereotype.Component;

@Component
public class SftpInbFilesSyncBoilerplateFactory {

    public SftpInboundFileSynchronizer create(
            SessionFactory<ChannelSftp.LsEntry> sessionFactory,
            VolumeConfig sourceVolumeConfig,
            VolumeConfigTaskMeta volumeTaskMeta)
    {
        SftpInboundFileSynchronizer fileSynchronizer = new SftpInboundFileSynchronizer(sessionFactory);

        Boolean deleteSourceFiles = volumeTaskMeta.getDeleteSourceFiles();
        if (deleteSourceFiles == null) {
            fileSynchronizer.setDeleteRemoteFiles(false);
        } else {
            fileSynchronizer.setDeleteRemoteFiles(deleteSourceFiles);
        }

        fileSynchronizer.setRemoteDirectory(sourceVolumeConfig.getPath());
        volumeTaskMeta.getFileExtensions().forEach(ext ->
                fileSynchronizer.setFilter(new SftpSimplePatternFileListFilter(ext)));

        return fileSynchronizer;
    }

}
