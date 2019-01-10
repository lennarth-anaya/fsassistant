package org.lrth.fsassistant.appcontext.boilerplatefactory;

import org.lrth.fsassistant.configuration.VolumeConfigTaskMeta;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.stereotype.Component;

@Component
public class FileWritingMessageHandlerBoilerplateFactory {
    public FileWritingMessageHandler create(VolumeConfigTaskMeta volumeConfigTaskMeta) {//PipeConfig config) {
        FileWritingMessageHandler fileWriterHandler;

        String localFolderToDownload = volumeConfigTaskMeta.getVolumeDef().getPath();

        Expression directoryExpression = new SpelExpressionParser().parseExpression(localFolderToDownload);

        fileWriterHandler = new FileWritingMessageHandler(directoryExpression);

        fileWriterHandler.setFileExistsMode(FileExistsMode.IGNORE);
        fileWriterHandler.setDeleteSourceFiles(volumeConfigTaskMeta.getDeleteSourceFiles());
        fileWriterHandler.setAutoCreateDirectory(volumeConfigTaskMeta.getAutoCreateDirectory());

        return fileWriterHandler;
    }
}
