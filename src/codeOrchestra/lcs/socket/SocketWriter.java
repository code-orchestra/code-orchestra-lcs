package codeOrchestra.lcs.socket;

/**
 * @author Alexander Eliseyev
 */
public interface SocketWriter {

  void writeToSocket(String str);
  
  void closeSocket();

}
