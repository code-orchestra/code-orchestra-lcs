package codeOrchestra.actionScript.liveCoding.run;

import java.util.Map;

import codeOrchestra.actionScript.liveCoding.LiveCodingSession;
import codeOrchestra.lcs.socket.SocketWriter;

/**
 * @author Alexander Eliseyev
 */
public class LiveCodingSessionImpl implements LiveCodingSession {

  private long startTimestamp;
  private SocketWriter socketWriter;
  private String broadcastId;  
  private String clientId;
  private Map<String, String> clientInfo;
  private int packageId = 1;

  public LiveCodingSessionImpl(String broadcastId, String clientId, Map<String, String> clientInfo, long startTimestamp, SocketWriter socketWriter) {
    this.clientId = clientId;
    this.broadcastId = broadcastId;
    this.clientInfo = clientInfo;
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

  public String getBroadcastId() {
    return broadcastId;
  }

  @Override
  public int getPackageNumber() {
    return packageId;
  }

  @Override
  public String getClientId() {
    return clientId;
  }

  @Override
  public Map<String, String> getClientInfo() {
    return clientInfo;
  }
  
  @Override
  public String getBasicClientInfo() {
    StringBuilder sb = new StringBuilder();
    sb.append(clientInfo.get("OS")).append(" ");
    sb.append(clientInfo.get("ARCH")).append(" ");
    sb.append(clientInfo.get("L")).append(" ");
    sb.append(clientInfo.get("R"));
    return sb.toString();
  }
  
  @Override
  public void incrementPackageNumber() {
    packageId++;    
  }

  @Override
  public synchronized void sendLiveCodingMessage(String message) {
    socketWriter.writeToSocket("livecoding::" + message + "::" + broadcastId + "::" + packageId);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((broadcastId == null) ? 0 : broadcastId.hashCode());
    result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    LiveCodingSessionImpl other = (LiveCodingSessionImpl) obj;
    if (broadcastId == null) {
      if (other.broadcastId != null)
        return false;
    } else if (!broadcastId.equals(other.broadcastId))
      return false;
    if (clientId == null) {
      if (other.clientId != null)
        return false;
    } else if (!clientId.equals(other.clientId))
      return false;
    return true;
  }

  
}
