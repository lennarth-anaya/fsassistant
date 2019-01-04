package org.lrth.fsassistant.appcontext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

@Configuration
public class Sftp {
    
    @Bean
    public DefaultSftpSessionFactory sftpSession() {
        DefaultSftpSessionFactory sessFactory = new DefaultSftpSessionFactory();
        sessFactory.setHost(host);
        sessFactory.setPrivateKey(privateKey);
        sessFactory.setPrivateKeyPassphrase(privateKeyPassphrase);
        // sessFactory.setPort(port);
        sessFactory.setUser(user);
        
        return sessFactory;
    }
}
