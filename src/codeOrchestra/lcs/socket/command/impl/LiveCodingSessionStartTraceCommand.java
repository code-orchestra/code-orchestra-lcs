package codeOrchestra.lcs.socket.command.impl;

import codeOrchestra.actionScript.logging.model.LoggerMessage;
import codeOrchestra.lcs.session.LiveCodingManager;
import codeOrchestra.lcs.socket.ClientSocketHandler;
import codeOrchestra.lcs.socket.command.TraceCommand;

/**
 * @author Alexander Eliseyev
 */
public class LiveCodingSessionStartTraceCommand implements TraceCommand {

  private static final String START_SESSION_COMMAND = "start-live-coding-session";

  @Override
  public boolean isApplicable(LoggerMessage message) {
    return START_SESSION_COMMAND.equals(message.getCommand());
  }

  @Override
  public void execute(LoggerMessage message, ClientSocketHandler clientSocketHandler) {
    LiveCodingManager liveCodingManager = LiveCodingManager.instance();

    // RF-1307 - we do this check in case no project is open, but a live-coding-compiled swf is run
    if (liveCodingManager != null) {
      liveCodingManager.startSession(message.getMessage(), clientSocketHandler);
    }
  }
}
