package org.lrth.fsassistant.appcontext;

import com.jcraft.jsch.ChannelSftp;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.filters.SftpSimplePatternFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

import javax.validation.constraints.NotNull;

@Configuration
@RequiredArgsConstructor
public class SftpCtx {

    @Value("fs-assistant.sftp-source.private-key")
    private Resource privateKey;

    @NotNull private  SftpProperties sftpCfg;
    @NotNull private SessionFactory<ChannelSftp.LsEntry> sessionFactory;

    @Bean
    public DefaultSftpSessionFactory sourceSftpSession() {
        DefaultSftpSessionFactory sessFactory = new DefaultSftpSessionFactory();
        sessFactory.setHost(sftpCfg.getHost());
        sessFactory.setPrivateKey(privateKey);
        sessFactory.setPrivateKeyPassphrase(sftpCfg.getPrivateKeyPassphrase());
        sessFactory.setPort(sftpCfg.getPort());
        sessFactory.setUser(sftpCfg.getUser());
        
        return sessFactory;
    }

    @Bean
    public SftpInboundFileSynchronizer sftpInboundFileSynchronizer() {
        SftpInboundFileSynchronizer fileSynchronizer = new SftpInboundFileSynchronizer(sessionFactory);
        fileSynchronizer.setDeleteRemoteFiles(false);
        fileSynchronizer.setRemoteDirectory("/");
        fileSynchronizer.setFilter(new SftpSimplePatternFileListFilter("*.xml"));
        return fileSynchronizer;
    }

    @Bean
    public SftpMessageHandler sourceSftpMessageHandler() {
        SftpMessageHandler handler = new SftpMessageHandler(sessionFactory);

        handler.setRemoteDirectoryExpression(new LiteralExpression(
                sftpRemoteDirectory));

        handler.setFileNameGenerator(new FileNameGenerator() {
            @Override
            public String generateFileName(Message<?> message) {
                if (message.getPayload() instanceof File) {
                    return ((File) message.getPayload()).getName();
                } else {
                    throw new IllegalArgumentException("File expected as payload.");
                }
            }
        });

        return handler;
    }

}
