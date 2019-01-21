package org.lrth.fsassistant.appcontext.boilerplatefactory;

import org.lrth.fsassistant.configuration.VolumeConfigTaskMeta;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileWritingMessageHandlerBoilerplateFactory {
    public FileWritingMessageHandler create(VolumeConfigTaskMeta volumeConfigTaskMeta) {//PipeConfig config) {
        FileWritingMessageHandler fileWriterHandler;

        String localFolderToDownload = volumeConfigTaskMeta.getVolumeDef().getPath();

        File file = new File(localFolderToDownload);

        fileWriterHandler = new FileWritingMessageHandler(file);
        fileWriterHandler.setFileExistsMode(FileExistsMode.IGNORE);
        fileWriterHandler.setDeleteSourceFiles(volumeConfigTaskMeta.isDeleteSourceFiles());
        fileWriterHandler.setAutoCreateDirectory(volumeConfigTaskMeta.isAutoCreateDirectory());
        fileWriterHandler.setExpectReply(false);

        return fileWriterHandler;
    }
}
