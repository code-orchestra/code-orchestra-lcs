package codeOrchestra.lcs.socket;

import java.net.Socket;

/**
 * @author Alexander Eliseyev
 */
public interface ClientSocketHandlerFactory {

  ClientSocketHandler createHandler(Socket socket);

}
