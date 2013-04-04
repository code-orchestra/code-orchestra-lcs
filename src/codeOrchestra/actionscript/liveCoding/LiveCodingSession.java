package codeOrchestra.actionScript.liveCoding;

import codeOrchestra.lcs.socket.SocketWriter;

/**
 * @author Alexander Eliseyev
 */
public interface LiveCodingSession {

  String getSessionId();

  long getStartTimestamp();

  SocketWriter getSocketWriter();
  
  int getPackageNumber();
  
  void incrementPackageNumber();
  
}