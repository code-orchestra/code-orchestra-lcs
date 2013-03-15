package codeOrchestra.lcs.socket.command.impl;

import codeOrchestra.actionscript.logging.model.LoggerMessage;
import codeOrchestra.actionscript.logging.model.LoggerMessageEncoder;
import codeOrchestra.actionscript.logging.model.LoggerScopeWrapper;
import codeOrchestra.actionscript.logging.scope.MessageScopeRegistry;
import codeOrchestra.lcs.logging.Level;
import codeOrchestra.lcs.logging.Logger;
import codeOrchestra.lcs.socket.ClientSocketHandler;
import codeOrchestra.lcs.socket.command.TraceCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public class LoggerTraceCommand implements TraceCommand {

  @Override
  public boolean isApplicable(LoggerMessage message) {
    return LoggerMessageEncoder.isLegitSeverityLevel(message.getCommand());
  }

  @Override
  public void execute(final LoggerMessage loggerMessage, ClientSocketHandler clientSocketHandler) {
    // Get the logger by the root name
    String rootSimpleName = loggerMessage.getRootSimpleName();
    Logger asLogger = Logger.getLogger(rootSimpleName != null ? rootSimpleName : "trace");

    // Severity
    Level severity = loggerMessage.getSeverity();

    // Scopes
    List<String> scopeIds = null;
    if (!loggerMessage.getScopes().isEmpty()) {
      scopeIds = new ArrayList<String>();
      for (LoggerScopeWrapper scopeWrapper : loggerMessage.getScopes()) {
        MessageScopeRegistry.getInstance().addOrUpdateScope(scopeWrapper.getId(), scopeWrapper.getName());

        scopeIds.add(scopeWrapper.getId());
      }
    }

    // Timestamp
    long timestamp = loggerMessage.getTimestamp();

    if (severity == Level.INFO || severity == Level.DEBUG) {
      asLogger.info(loggerMessage.getMessage(), scopeIds, timestamp);
    } else if (severity == Level.WARN) {
      asLogger.warning(loggerMessage.getMessage(), scopeIds, timestamp);
    } else if (severity == Level.ERROR || severity == Level.FATAL) {
      asLogger.error(loggerMessage.getMessage(), scopeIds, timestamp);
    }
  }

}
