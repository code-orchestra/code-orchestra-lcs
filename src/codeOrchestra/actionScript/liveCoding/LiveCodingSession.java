package codeOrchestra.actionScript.liveCoding;

import java.util.Map;

import codeOrchestra.lcs.socket.SocketWriter;

/**
 * @author Alexander Eliseyev
 */
public interface LiveCodingSession {

  String getClientId();
  
  String getBroadcastId();
  
  String getBasicClientInfo();
  
  Map<String, String> getClientInfo();

  long getStartTimestamp();

  SocketWriter getSocketWriter();
  
  void sendLiveCodingMessage(String message, String packageId, boolean addToHistory);
  
  void sendMessageAsIs(String message);
  
  int getSessionNumber();
  
  void dispose();
  
  boolean isDisposed();
  
}