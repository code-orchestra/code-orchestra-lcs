package codeOrchestra.lcs.socket.command;

import codeOrchestra.actionscript.logging.model.LoggerMessage;
import codeOrchestra.lcs.socket.ClientSocketHandler;

/**
 * @author Alexander Eliseyev
 */
public interface TraceCommand {

  boolean isApplicable(LoggerMessage message);
  void execute(LoggerMessage message, ClientSocketHandler clientSocketHandler);

}
