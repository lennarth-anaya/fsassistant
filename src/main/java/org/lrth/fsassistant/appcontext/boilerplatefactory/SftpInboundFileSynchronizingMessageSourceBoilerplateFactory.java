package org.lrth.fsassistant.appcontext.boilerplatefactory;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.jcraft.jsch.ChannelSftp;
import lombok.RequiredArgsConstructor;
import org.lrth.fsassistant.configuration.VolumeConfig;
import org.lrth.fsassistant.configuration.VolumeConfigTaskMeta;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.stereotype.Component;


import java.io.File;

@RequiredArgsConstructor
@Component
public class SftpInboundFileSynchronizingMessageSourceBoilerplateFactory {

    private final SftpInbFilesSyncBoilerplateFactory sftpSyncFactory;
    private final SftpSessionBoilerplateFactory sftpSessionBoilerplateFactory;

    public MessageSource<File> create(
            VolumeConfigTaskMeta sourceTaskMeta,
            VolumeConfigTaskMeta targetTaskMeta) {
        SftpInboundFileSynchronizingMessageSource source;

        VolumeConfig sourceVolumeConfig = sourceTaskMeta.getVolumeDef();

        SessionFactory<ChannelSftp.LsEntry> sessionFactory = sftpSessionBoilerplateFactory
                .create(sourceVolumeConfig);

        SftpInboundFileSynchronizer sftpFileSynchronizer = sftpSyncFactory.create(
                sessionFactory, sourceVolumeConfig, sourceTaskMeta);

        source = new SftpInboundFileSynchronizingMessageSource(sftpFileSynchronizer);

        source.setLocalDirectory(new File(targetTaskMeta.getVolumeDef().getPath()));
        source.setAutoCreateLocalDirectory(targetTaskMeta.isAutoCreateDirectory());
        source.setLocalFilter(new AcceptOnceFileListFilter<>());

        // avoid silly "No beanFactory" warning shown with an alarmist RuntimeException
        LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("org.springframework.integration.expression.ExpressionUtils");
        rootLogger.setLevel(Level.ERROR);

        return source;
    }
}
