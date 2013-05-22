package codeOrchestra.lcs.rpc.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import codeOrchestra.utils.StringUtils;

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
  
  private transient Map<String, String> shortCodeToToken = new HashMap<String, String>();
  
  public String createNewTokenAndGetShortCode() {
    // TODO: implement
    return "1234";
  }
  
  public void addAuthToken(String token) {
    List<String> authTokens = getPersistedAuthTokens();
    if (!authTokens.contains(authTokens)) {
      authTokens.add(token);      
    }
    
    storeAuthTokens(authTokens);
  }
  
  private void storeAuthTokens(List<String> authTokens) {
    StringBuilder sb = new StringBuilder();    
    Iterator<String> iterator = authTokens.iterator();
    while (iterator.hasNext()) {
      String token = iterator.next();
      sb.append(token);
      
      if (iterator.hasNext()) {
        sb.append("|");
      }
    }
    preferences.put(TOKENS_KEY, sb.toString());
    try {
      preferences.sync();
    } catch (BackingStoreException e) {
      throw new RuntimeException("Error while storing auth tokens", e);
    }
  }

  private List<String> getPersistedAuthTokens() {
    List<String> result = new ArrayList<String>();
    
    String tokensString = preferences.get(TOKENS_KEY, "");
    String[] tokensSplit = tokensString.split("\\|");
    if (tokensSplit != null) {
      for (String token : tokensSplit) {
        if (StringUtils.isNotEmpty(token)) {
          result.add(token);
        }
      }
    }
    
    return result;
  }

  public boolean isValidToken(String token) {
    return getPersistedAuthTokens().contains(token);
  }
  
}
