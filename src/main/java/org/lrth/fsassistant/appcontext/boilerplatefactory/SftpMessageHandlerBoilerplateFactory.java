package org.lrth.fsassistant.appcontext.boilerplatefactory;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import org.lrth.fsassistant.configuration.VolumeConfig;
import org.lrth.fsassistant.configuration.VolumeConfigTaskMeta;
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

    public SftpMessageHandler create(VolumeConfigTaskMeta config) {
        SftpMessageHandler handler;

        VolumeConfig volumeDef = config.getVolumeDef();
        String sftpRemoteDirectory = volumeDef.getPath();

        SessionFactory<LsEntry> sessionFactory = sftpSessionBoilerplateFactory.create(volumeDef);

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
