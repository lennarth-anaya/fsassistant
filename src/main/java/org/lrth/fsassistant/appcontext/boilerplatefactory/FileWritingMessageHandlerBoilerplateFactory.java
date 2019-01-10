package org.lrth.fsassistant.appcontext.boilerplatefactory;

import org.lrth.fsassistant.configuration.PipeConfig;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.stereotype.Component;

@Component
public class FileWritingMessageHandlerBoilerplateFactory {
    public FileWritingMessageHandler create(PipeConfig config) {
        FileWritingMessageHandler fileWriterHandler;

        String localFolderToDownload = config.getTargetVolumeConfig().getPath();

        Expression directoryExpression = new SpelExpressionParser().parseExpression(localFolderToDownload);

        fileWriterHandler = new FileWritingMessageHandler(directoryExpression);

        fileWriterHandler.setFileExistsMode(FileExistsMode.IGNORE);
        fileWriterHandler.setDeleteSourceFiles(config.getSourceVolumeMeta().getDeleteSourceFiles());
        fileWriterHandler.setAutoCreateDirectory(config.getTargetVolumeMeta().getAutoCreateDirectory());

        return fileWriterHandler;
    }
}
