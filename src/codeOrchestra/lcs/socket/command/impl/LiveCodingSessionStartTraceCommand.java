package codeOrchestra.lcs.socket.command.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import codeOrchestra.actionScript.logging.model.LoggerMessage;
import codeOrchestra.lcs.session.LiveCodingManager;
import codeOrchestra.lcs.socket.ClientSocketHandler;
import codeOrchestra.lcs.socket.command.TraceCommand;

/**
 * @author Alexander Eliseyev
 */
public class LiveCodingSessionStartTraceCommand implements TraceCommand {

  private static final String START_SESSION_COMMAND = "start-live-coding-session";

  @Override
  public boolean isApplicable(LoggerMessage message) {
    return START_SESSION_COMMAND.equals(message.getCommand());
  }

  @Override
  public void execute(LoggerMessage message, ClientSocketHandler clientSocketHandler) {
    LiveCodingManager liveCodingManager = LiveCodingManager.instance();

    // RF-1307 - we do this check in case no project is open, but a live-coding-compiled swf is run
    if (liveCodingManager != null) {
      String sessionId = message.getMessage();
      String[] sessionIdSplit = sessionId.split(":");
      
      String broadcastId = sessionIdSplit[0];
      String clientId = sessionIdSplit[1];
      String clientInfo = sessionIdSplit[2];
      
      String decoded = null;
      try {      
        decoded = URLDecoder.decode(clientInfo, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        // can't happen
      }
      
      Map<String, String> clientInfoDictionary = new HashMap<String, String>();
      String[] decodedSplit = decoded.split("&");
      for (int i = 0; i < decodedSplit.length; i++) {
        String keyValue = decodedSplit[i];
        String[] keyValueSplit = keyValue.split("=");      
        clientInfoDictionary.put(keyValueSplit[0], keyValueSplit[1]);
      }
      
      liveCodingManager.startSession(broadcastId, clientId, clientInfoDictionary, clientSocketHandler);
    }
  }
  
  public static void main(String[] args) {
    String cliendInfo = "A=t&SA=t&SV=t&EV=t&MP3=t&AE=t&VE=t&ACC=f&PR=t&SP=f&SB=f&DEB=t&V=MAC%2011%2C4%2C402%2C287&M=Adobe%20Macintosh&R=1920x1200&COL=color&AR=1.0&OS=Mac%20OS%2010.8.2&ARCH=x86&L=en&PR32=t&PR64=t&PT=StandAlone&AVD=f&LFD=f&WD=t&TLS=t&ML=5.1&DP=72";
    
    String decoded = null;
    try {      
      decoded = URLDecoder.decode(cliendInfo, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      // can't happen
    }
    
    Map<String, String> clientInfoDictionary = new HashMap<String, String>();
    String[] decodedSplit = decoded.split("&");
    for (int i = 0; i < decodedSplit.length; i++) {
      String keyValue = decodedSplit[i];
      String[] keyValueSplit = keyValue.split("=");      
      clientInfoDictionary.put(keyValueSplit[0], keyValueSplit[1]);
    }
    
    System.out.println();
  }
  
}
