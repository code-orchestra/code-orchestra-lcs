package codeOrchestra.actionScript.liveCoding.run;

import codeOrchestra.actionScript.liveCoding.LiveCodingSession;
import codeOrchestra.lcs.socket.SocketWriter;

/**
 * @author Alexander Eliseyev
 */
public class LiveCodingSessionImpl implements LiveCodingSession {

  private long startTimestamp;
  private SocketWriter socketWriter;
  private String sessionId;
  private int packageId = 1;

  public LiveCodingSessionImpl(String sessionId, long startTimestamp, SocketWriter socketWriter) {
    this.sessionId = sessionId;
    this.startTimestamp = startTimestamp;
    this.socketWriter = socketWriter;
  }

  public SocketWriter getSocketWriter() {
    return socketWriter;
  }

  @Override
  public long getStartTimestamp() {
    return startTimestamp;
  }

  public String getSessionId() {
    return sessionId;
  }

  @Override
  public int getPackageNumber() {
    return packageId;
  }

  @Override
  public void incrementPackageNumber() {
    packageId++;    
  }

  @Override
  public void sendLiveCodingMessage(String message) {
    socketWriter.writeToSocket("livecoding::" + message + "::" + sessionId + "::" + packageId);
  }
  
}
