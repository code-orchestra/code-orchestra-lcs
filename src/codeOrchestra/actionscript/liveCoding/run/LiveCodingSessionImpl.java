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
}
