package codeOrchestra.actionscript.logging.transport;

import codeOrchestra.lcs.socket.ClientSocketHandler;
import codeOrchestra.lcs.socket.ClientSocketHandlerFactory;
import codeOrchestra.lcs.socket.command.TraceBasedCommandExecutor;

import java.net.Socket;

/**
 * @author Alexander Eliseyev
 */
public final class LoggerClientSocketHandlerFactory implements ClientSocketHandlerFactory {

  private final static ClientSocketHandlerFactory INSTANCE = new LoggerClientSocketHandlerFactory();

  public static ClientSocketHandlerFactory getInstance() {
    return INSTANCE;
  }

  private LoggerClientSocketHandlerFactory() {
  }

  public ClientSocketHandler createHandler(Socket socket) {
    return new TraceBasedCommandExecutor(socket);
  }
}
