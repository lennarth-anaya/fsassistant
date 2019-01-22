## What is this project for?

Assistant to manage files, especially auto-generated files whose management should also be automated. "Managed" so far means moving files from different locations (to possibly monitor or process them) and cleaning them up to avoid using too much space, perhaps when they are old enough not to be relevant.

## Scope

1. It's target is small non-distributed applications so far, but using infrastructure that would allow to migrate to something more corporate.

2. It currently supports next file system:
> - Local file system folders
> - SFTP remote folders

3. Operations:
> - Files move
> - Folders cleanup (files deletion)

## SFTP preconditions

If you're going to use an SFTP server, it is encouraged to use user/RSA-keys authentication rather than user/password.

1. Generate your RSA key in your **client** computer's command line (it could be installed/used on Windows, Linux, Mac):

> ```ssh-keygen -t rsa```

> - Command above will ask you for certain parameters to generate yopur public and private keys on your local file system. The passphrase you define while creating your private key here will be used later on, so write it down somewhere or make sure you will recall it.

> - You could reuse this same key for every SFTP server your application will connect to. 

2. Instruct each SFTP **server** to accept such a RSA key:

> Copy the whole content from your just-created  **xxx-rsa.pub** file that is on your **client** computer (the xxx-rsa.pub file was created on step 1)

> Paste the content of the public key copied above to **~/.ssh/authorized_keys** file on your **server** (add the line if there are already other keys, or create the file if it doesn't even exist)


## Configuration file

Perhaps in a future it will be enhanced to get rid of the need of coding and only define sources and destinies in this file. So far, it is required to do both, define setting in configuration file, and create the integration source code to bind Spring Integration components in correspondance to such a configuration.

Configuration file must respect spaces indentation according to [Yaml](https://yaml.org/) standard.

### Configuration root

> fs-assistant:
This is the root of configuration file and cannot be changed.

#### Volumes configuration

>>   volumes:
This label can neither be changed and represent the root of the different folders you are going to read (source) or write (target) on every task. Volumes are defined independently to tasks so they can be reused by several tasks. Any amount of volumes can be defined to be processed.

>>> volume-definition:
The label "volume-definition" itself must be defined by you and must be unique in the same file if other volumes are also specified.

##### Local folder definition example:

```
fs-assistant:
  volumes:
    folderToCleanup:
      type: local
      path: C:\Users\me\Documents\test\cleanup
```

##### SFTP folder definition example:

```
fs-assistant:
  volumes:
    someFolderSftp001:
      type: sftp
      path: ./test
      host: localhost
      port: 22
      user: yourSftpUser
      private-key: C:\Users\me\.ssh\id_sftptest_rsa
      private-key-passphrase: yourPrivateKeyPassphrase
      # password is optional if no private-key & passphrase were used
      # password:  
```

### Pipes (this label could be changed to something more descriptive)

>>   pipes:
This label cannot be changed and will contain the defitinions of the tasks that are going to pick files up from source folders and/or drop them off on the target folders. According to Spring Integration architecture, we can define sources and targets (sinks, in Spring's jargot) independently, so sources and targets can be reused or inter-changed however it suits our changing needs. In this guide we will exemplify the definition of these elements with source and target volumes, solely with source or solely with target.

Every 'pipe' definition must have its own unique arbitrary label and contain:

>>> task:
>>>> cron:
Detailing how often the task must check for new files in the source folder
>>>> force-suspend:
If the specific task must be suspended whether cron expression above fulfills or not.

>>>> max-messages-per-poll:
How many messages we expect to efficiently process per task execution. A too large number could stress your CPU too much without giving it a breath, while a too small number would cause files management to always run behind the process that is creating such files.

>>> source-volume-meta:
>>> target-volume-meta:
Source and target volumes bind folder definitions to tasks, and attach certain metadata information specific to the task: like file extensions to work with; mininmum number of seconds lapsed since last creation/modification each file has to have in order to be considered by the task (useful if you want the files to be kept in the source for certain periods of time before moving from them); if folders must be excluded, etc.

## Examples

Examples are always the best way to understand things. Feel free to ask questions if you rather inspect the source code and would like to understand something to replicate it on your own, or an example is not self explanatory.

Examples below will include only the portion of the same configuration file that is of interest for the example. There is only one configuration file and its name is **application.yml**. Examples that reuse definitions from previous examples will depict it with three dots, indicating there are other configuration parameters not shown but defined before in other examples.

### Cleaning up local folder

<table>
  <tr>
    <th>Config</th>
    <th>Code</th>
  </tr>
  <tr>
    <td>
      <pre>
fs-assistant:
  volumes:
    folderCleanup:
      type: local
      path: C:\Users\me\Documents\test\cleanup
  pipes:
    local-cleanup-pipe:
      task:
        # Order: second minute hour day-of-month month day-of-week year
        cron: 0/30 * * * * *
        force-suspend: false
        max-messages-per-poll: 30
      source-volume-meta:
        volume-ref: folderCleanup
        max-expected-files: 1000
        filter:
          file-modification-min-age-seconds: 172800
          filter-out-folders: false
          file-extensions:
          - "*.txt"
          - "*.dat"
      </pre>
    </td>
    <td>
      <pre>
package org.lrth.fsassistant.integration;

import org.lrth.fsassistant.appcontext.boilerplatefactory.FileReadingMessageSourceBoilerplateFactory;
import org.lrth.fsassistant.configuration.AppConfig;
import org.lrth.fsassistant.configuration.PipeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.core.MessageSource;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;

import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;

import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
@RequiredArgsConstructor
public class LocalCleanupPipe {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalCleanupPipe.class);

    private final AppConfig appConfig;
    private final FileReadingMessageSourceBoilerplateFactory factory;
    
    private static final String PIPE_ID = "local-cleanup-pipe";

    private PipeConfig config;

    @PostConstruct
    public void init() {
        this.config = appConfig.getPipes().get(PIPE_ID);
    }

    /* ****** PIPE SOURCE ******* */
    
    @Bean
    @InboundChannelAdapter(
        channel = "fileToRemove",
        poller = @Poller(
            cron = "${fs-assistant.pipes.local-cleanup-pipe.task.cron}",
            maxMessagesPerPoll = "${fs-assistant.pipes.local-cleanup-pipe.task.max-messages-per-poll}"
        )
    )
    public MessageSource<File> localFolderPollerForCleanup() {

        if (config.getTask().isForceSuspend()) {
            return null;
        }

        // we want to pick folders again once they're empty
        final boolean pickFilesOnlyOnce = false;

        // TODO there should be a smart way of debugging configId
        final String configId = PIPE_ID + ".sourceVolumeMeta";

        return factory.create(config.getSourceVolumeMeta(), pickFilesOnlyOnce, configId);
    }

    /* ****** PIPE SINK ******* */
    @Bean
    @ServiceActivator(
        inputChannel = "fileToRemove"
    )
    public MessageHandler folderCleaner() {
        return message -> {
            if (config.getTask().isForceSuspend()) {
                return;
            }

            File file = (File) message.getPayload();

            if (file.isDirectory() && file.list().length == 0) {
                // cleaning up empty folders too, no config parameter that would complicate VolumeConfigTaskMeta structure
                deleteFile(file);
            }

            if (file.isFile()) {
                deleteFile(file);
            }
        };
    }

    public void deleteFile(File file) {
        if ( config.getTask().isSimulationMode() ) {
            LOGGER.info("SIMULATION MODE: I would have deleted file: [{}]", file);
        } else {
            LOGGER.info("DELETING: [{}]", file);
            file.delete();
        }
    }
}
      </pre>
    </td>
  </tr>
</table>


### Pulling files from a remote location via SFTP to local folder

<table>
  <tr>
    <th>Config</th>
    <th>Code</th>
  </tr>
  <tr>
    <td>
      <pre>
fs-assistant:
  volumes:
    folderCleanup:
      type: local
      path: C:\Users\me\Documents\test\cleanup
    remoteSftp:
      type: sftp
      path: ./test
      host: localhost
      port: 22
      user: user
      private-key: C:\Users\me\.ssh\id_sftptest_rsa
      private-key-passphrase: password
    move-from-sftp-to-local-pipe:
      task:
        # Order: second minute hour day-of-month month day-of-week year
        cron: 0/30 * * * * *
        force-suspend: true
        simulation-mode: false
        max-messages-per-poll: 30
      source-volume-meta:
        volume-ref: remoteSftp
        delete-source-files: true
        filter:
          # 172800 seconds = 2 days
          file-modification-min-age-seconds: 172800
          file-extensions:
          - "*.txt"
          - "*.dat"
      target-volume-meta:
        volume-ref: folderCleanup
        auto-create-directory: true
      </pre>
    </td>
    <td>
      <pre>
package org.lrth.fsassistant.integration;

import lombok.RequiredArgsConstructor;
import org.lrth.fsassistant.appcontext.boilerplatefactory.FileWritingMessageHandlerBoilerplateFactory;
import org.lrth.fsassistant.appcontext.boilerplatefactory.SftpInboundFileSynchronizingMessageSourceBoilerplateFactory;
import org.lrth.fsassistant.configuration.AppConfig;
import org.lrth.fsassistant.configuration.PipeConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
@RequiredArgsConstructor
public class SftpToLocalPipe {

    private static final String PIPE_ID = "move-from-sftp-to-local-pipe";

    private final AppConfig appConfig;

    private PipeConfig config;

    @PostConstruct
    public void init() {
        this.config = appConfig.getPipes().get(PIPE_ID);
    }


    /* ****** PIPE SOURCE ******* */

    private final SftpInboundFileSynchronizingMessageSourceBoilerplateFactory msgSourceFactory;

    @Bean
    @InboundChannelAdapter(
        value = "localStore",
        poller = @Poller(
            cron = "${fs-assistant.pipes.move-from-sftp-to-local-pipe.task.cron}",
            maxMessagesPerPoll = "${fs-assistant.pipes.move-from-sftp-to-local-pipe.task.max-messages-per-poll}"
        )
    )
    public MessageSource<File> pollFiles() {
        if (config.getTask().isForceSuspend()) {
            return null;
        }

        return msgSourceFactory.create(config.getSourceVolumeMeta(),
                config.getTargetVolumeMeta());
    }

    /* ****** PIPE SINK ******* */

    private final FileWritingMessageHandlerBoilerplateFactory factory;

    @Bean
    @ServiceActivator(inputChannel = "localStore")
    public MessageHandler localPipeWriter() {
        if (config.getTask().isForceSuspend()) {
            return null;
        }

        return factory.create(config.getTargetVolumeMeta());
    }
}
      </pre>
    </td>
  </tr>
</table>


### Moving files from a remote location to another remote location, both accessible via SFTP

Example below will just depict the target component and configuration, demonstrating how to reuse components and chaing them, you would just need to point component from above example from:

```
    @InboundChannelAdapter(
        value = "localStore",
```

To:

```
    @InboundChannelAdapter(
        value = "remoteStore",
```

And comment out (also in above class), to avoid facing validation errors that localStore would not receive an input from anyone:

```
    @ServiceActivator(inputChannel = "localStore")
```

Then below component could be the target of source defined in class of previous example:

<table>
  <tr>
    <th>Config</th>
    <th>Code</th>
  </tr>
  <tr>
    <td>
      <pre>
fs-assistant:
  volumes:
    ...
    remoteSftp2:
      type: sftp
      path: ./test2
      host: localhost
      port: 22
      user: user
      private-key: C:\Users\me\.ssh\id_sftptest_rsa
      private-key-passphrase: passphrase
    move-from-sftp-to-sftp-pipe:
      target-volume-meta:
        volume-ref: remoteSftp2
        auto-create-directory: true
      </pre>
    </td>
    <td>
      <pre>
package org.lrth.fsassistant.integration;

import lombok.RequiredArgsConstructor;
import org.lrth.fsassistant.appcontext.boilerplatefactory.SftpMessageHandlerBoilerplateFactory;
import org.lrth.fsassistant.configuration.AppConfig;
import org.lrth.fsassistant.configuration.PipeConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.*;
import org.springframework.messaging.MessageHandler;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
@RequiredArgsConstructor
public class SftpToSftpPipe {
    private static final String PIPE_ID = "move-from-sftp-to-sftp-pipe";

    private final AppConfig appConfig;
    private PipeConfig config;

    @PostConstruct
    public void init() {
        this.config = appConfig.getPipes().get(PIPE_ID);
    }

    /* ****** PIPE SOURCE ******* */

    // we are going to reuse source on SftpToLocalPipe pointing its channel to below one

    /* ****** PIPE SINK ******* */

    private final SftpMessageHandlerBoilerplateFactory factory;

    @Bean
    @ServiceActivator(inputChannel = "remoteStore")
    public MessageHandler sftpSink() {
        return factory.create(config.getTargetVolumeMeta());
    }

    /** Alternative to inputChannel, this gateway allows other java classes
     * to upload files via POJO method call
     */
    @MessagingGateway
    public interface UploadGateway {
        @Gateway(requestChannel = "remoteStore")
        void upload(File file);
    }

}
      </pre>
    </td>
  </tr>
</table>


### Moving files from a local folder to a remote location via SFTP

In this example we could also reuse previous class (target or sink), so we just have to define the local source and point its output to "remoteStore" channel. Look how we are reusing "folderCleanup" volume definition defined previously in configuration file.

<table>
  <tr>
    <th>Config</th>
    <th>Code</th>
  </tr>
  <tr>
    <td>
      <pre>
fs-assistant:
    ...
    move-from-local-to-sftp-pipe:
      task:
        # Order: second minute hour day-of-month month day-of-week year
        cron: 0/30 * * * * *
        force-suspend: false
        simulation-mode: false
        max-messages-per-poll: 30
      source-volume-meta:
        volume-ref: folderCleanup
        delete-source-files: true
        # next does not filter, but just keeps a max size of files to recall before potentially grab the same files again
        max-expected-files: 1000
        filter:
          # 172800 seconds = 2 days
          file-modification-min-age-seconds: 60
          # subfolders are always processed recursively, nex param states if folder itself will be handled
          filter-out-folders: false
          file-extensions:
          - "*.h264"
          - "*.avi"
      </pre>
    </td>
    <td>
      <pre>
package org.lrth.fsassistant.integration;

import lombok.RequiredArgsConstructor;
import org.lrth.fsassistant.appcontext.boilerplatefactory.FileReadingMessageSourceBoilerplateFactory;
import org.lrth.fsassistant.configuration.AppConfig;
import org.lrth.fsassistant.configuration.PipeConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
@RequiredArgsConstructor
public class LocalToSftpPipe {

    private static final String PIPE_ID = "move-from-local-to-sftp-pipe";

    private final AppConfig appConfig;
    private PipeConfig config;

    @PostConstruct
    public void init() {
        this.config = appConfig.getPipes().get(PIPE_ID);
    }

    private final FileReadingMessageSourceBoilerplateFactory factory;


    // WARNING: This is not deleting source file. It could be used the processAfterCommit
    //  by adding a transaction context, or just use the cleanup pipe provided in this project.
    /**
     * We could have used UploadGateway, but using Spring Integration Channels instead.
     * @return
     */
    @Bean
    @InboundChannelAdapter(
        value = "remoteStore",
        poller = @Poller(
            cron = "${fs-assistant.pipes.move-from-local-to-sftp-pipe.task.cron}",
            maxMessagesPerPoll = "${fs-assistant.pipes.move-from-local-to-sftp-pipe.task.max-messages-per-poll}"
        )
    )
    public MessageSource<File> localFolderPoller() {
        if( config.getTask().isForceSuspend() ) {
            return null;
        }

        // TODO sending PIPE_ID is dirty
        return factory.create(config.getSourceVolumeMeta(), true, PIPE_ID);
    }
}
      </pre>
    </td>
  </tr>
</table>

