package codeOrchestra.lcs.rpc.security;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

public class COLTRemoteSecuriryManager {

  private static COLTRemoteSecuriryManager instance;
  
  public synchronized static COLTRemoteSecuriryManager getInstance() {
    if (instance == null) {
      instance = new COLTRemoteSecuriryManager();
    }
    return instance;
  }
  
  private static final String TOKENS_KEY = "coltRPCAuthKeys";
  
  private final Preferences preferences = Preferences.userNodeForPackage(COLTRemoteSecuriryManager.class);
  private Map<String, String> shortCodeToToken = new HashMap<String, String>();
  
  public String createNewTokenAndGetShortCode() {
    // TODO: implement
    return "1234";
  }
  
  public void addAuthToken() {
    // TODO: implement
  }
  
  public boolean isValidToken(String token) {
    // TODO: implement
    return true;
  }
  
}
