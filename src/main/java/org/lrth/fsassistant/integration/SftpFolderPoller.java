package org.lrth.fsassistant.integration;

import org.lrth.fsassistant.configuration.FileDownloaderConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;

import javax.validation.constraints.NotNull;
import java.io.File;

public class SftpFolderPoller {

    @NotNull private SftpInboundFileSynchronizer downloaderSftpFileSynchronizer;
    @NotNull private FileDownloaderConfig config;

    @Bean
    @InboundChannelAdapter(
        value = "${fs-assistant.file-downloader.task.channel}",
        poller = @Poller(trigger = "filesDownloaderTrigger")
    )
    public MessageSource<File> pollFiles() {
        SftpInboundFileSynchronizingMessageSource source =
            new SftpInboundFileSynchronizingMessageSource(downloaderSftpFileSynchronizer);

        source.setLocalDirectory(new File(config.getTargetVolumeConfig().getPath()));
        source.setAutoCreateLocalDirectory(
                config.getTargetVolumeMeta().getAutoCreateDirectory());
        source.setLocalFilter(new AcceptOnceFileListFilter<>());

        return source;
    }
}
