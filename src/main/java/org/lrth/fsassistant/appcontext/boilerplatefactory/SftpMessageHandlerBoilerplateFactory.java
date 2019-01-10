package org.lrth.fsassistant.appcontext.boilerplatefactory;

import com.jcraft.jsch.ChannelSftp;
import org.lrth.fsassistant.configuration.PipeConfig;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.File;

@Component
public class SftpMessageHandlerBoilerplateFactory {

    @NotNull
    private SftpSessionBoilerplateFactory sftpSessionBoilerplateFactory;

    public SftpMessageHandler create(PipeConfig config) {
        SftpMessageHandler handler;

        String sftpRemoteDirectory = config.getTargetVolumeConfig().getPath();

        SessionFactory<ChannelSftp.LsEntry> sessionFactory = sftpSessionBoilerplateFactory
            .create(config.getSourceVolumeConfig());

        handler = new SftpMessageHandler(sessionFactory);
        handler.setRemoteDirectoryExpression(new LiteralExpression(sftpRemoteDirectory));
        handler.setFileNameGenerator(message -> {
            if (message.getPayload() instanceof File) {
                return ((File) message.getPayload()).getName();
            } else {
                throw new IllegalArgumentException("Expected Payload to be a File");
            }
        });

        return handler;
    }
}
