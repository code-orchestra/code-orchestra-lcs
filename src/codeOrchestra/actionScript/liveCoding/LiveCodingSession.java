package codeOrchestra.actionScript.liveCoding;

import codeOrchestra.lcs.socket.SocketWriter;

/**
 * @author Alexander Eliseyev
 */
public interface LiveCodingSession {

  String getClientId();
  
  String getBroadcastId();
  
  String getClientInfo();

  long getStartTimestamp();

  SocketWriter getSocketWriter();
  
  int getPackageNumber();
  
  void incrementPackageNumber();
  
  void sendLiveCodingMessage(String message);
  
}