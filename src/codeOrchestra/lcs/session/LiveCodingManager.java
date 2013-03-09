package codeOrchestra.lcs.session;

import codeOrchestra.lcs.socket.ClientSocketHandler;

/**
 * @author Alexander Eliseyev
 */
public class LiveCodingManager {

  private static final LiveCodingManager instance = new LiveCodingManager();
  
  public static LiveCodingManager instance() {
    return instance;
  }

  public void startSession(String message, ClientSocketHandler clientSocketHandler) {
    // TODO: implement
  }

}
