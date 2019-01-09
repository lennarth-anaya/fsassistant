package org.lrth.fsassistant.integration;

import org.lrth.fsassistant.configuration.FileUploaderConfig;
import org.lrth.fsassistant.configuration.VolumeConfig;
import org.lrth.fsassistant.configuration.VolumeConfigTaskMeta;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.ChainFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.sftp.filters.SftpPersistentAcceptOnceFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;

import javax.validation.constraints.NotNull;
import java.io.File;

public class LocalFolderPoller {

    @NotNull private SftpInboundFileSynchronizer localFolderSynchronizer;
    @NotNull private FileUploaderConfig localFolderPollerConfig;

    /**
     * We could have used UploadGateway, but using Spring Integration Channels instead.
     * @return
     */
    @Bean
    @InboundChannelAdapter(
            value = "${fs-assistant.file-uploader.task.channel}",
            poller = @Poller(trigger = "filesUploaderTrigger")
    )
    public MessageSource<File> fileReadingMessageSource() {
        ChainFileListFilter<File> filters = new ChainFileListFilter<>();
        VolumeConfig volumeConfig = localFolderPollerConfig.getSourceVolumeConfig();
        VolumeConfigTaskMeta taskConfig = localFolderPollerConfig.getSourceVolumeMeta();

        // add all file extensions from configuration
        taskConfig.getFileExtensions().forEach(ext ->
            filters.addFilter(new SimplePatternFileListFilter(ext)));

        filters.addFilter(new AcceptOnceFileListFilter<>(
            taskConfig.getMaxExpectedFiles()));

        FileReadingMessageSource source = new FileReadingMessageSource();

        source.setAutoCreateDirectory(true);
        source.setDirectory(new File(volumeConfig.getPath()));

        source.setFilter(filters);

        return source;
    }

}
