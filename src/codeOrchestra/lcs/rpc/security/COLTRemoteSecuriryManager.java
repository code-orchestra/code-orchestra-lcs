package codeOrchestra.lcs.rpc.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import codeOrchestra.utils.StringUtils;

/**
 * @author Alexander Eliseyev
 */
public class COLTRemoteSecuriryManager {

  private static final String TOKENS_KEY = "coltRPCAuthKeys";
  private static final int MAX_TOKENS_COUNT = 100;
  private static final int MAX_FAILED_ATTEMPTS = 3;  
  
  private static COLTRemoteSecuriryManager instance;
  
  public synchronized static COLTRemoteSecuriryManager getInstance() {
    if (instance == null) {
      instance = new COLTRemoteSecuriryManager();
    }
    return instance;
  }
  
  private final Preferences preferences = Preferences.userNodeForPackage(COLTRemoteSecuriryManager.class);
  
  private List<COLTRemoteSecurityListener> listeners = new ArrayList<COLTRemoteSecurityListener>();
  
  private transient Map<String, String> shortCodeToToken = new HashMap<String, String>();
  private transient List<String> tokensCache;
  
  private int failedAttempts;
  private boolean forcePause;
  
  public void addListener(COLTRemoteSecurityListener listener) {
    listeners.add(listener);
  }

  public void removeListener(COLTRemoteSecurityListener listener) {
    listeners.remove(listener);
  }

  private void fireRequestEvent(String shortCode, String requestor) {
    for (COLTRemoteSecurityListener listener : listeners) {
      listener.onNewRequest(requestor, shortCode);
    }
  }
  
  private void fireSucessfullAuthEvent(String shortCode) {
    for (COLTRemoteSecurityListener listener : listeners) {
      listener.onSuccessfulActivation(shortCode);
    }
  }
  
  public synchronized String obtainAuthToken(String shortCode) throws TooManyFailedCodeTypeAttemptsException, InvalidShortCodeException {
    String authToken = shortCodeToToken.get(shortCode);
    if (authToken == null) {
      failedAttempts++;
      
      if (failedAttempts > MAX_FAILED_ATTEMPTS) {
        failedAttempts = 0;
        shortCodeToToken.clear();
        forcePause = true;
        throw new TooManyFailedCodeTypeAttemptsException();
      }
      
      throw new InvalidShortCodeException();
    }
    
    shortCodeToToken.remove(shortCode);
    addAuthToken(authToken);
    fireSucessfullAuthEvent(shortCode);
    invalidateCache();
    
    return authToken;
  }
  
  private void invalidateCache() {
    tokensCache = null;    
  }

  public synchronized String createNewTokenAndGetShortCode(String requestor) {
    if (forcePause) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        // ignore
      }  
      
      forcePause = false;
    }
    
    String shortCode = StringUtils.generateIdNumeric(4);
    String authToken = StringUtils.generateId();    
    
    shortCodeToToken.put(shortCode, authToken);
    
    fireRequestEvent(shortCode, requestor);
    
    return shortCode;
  }
  
  public void addAuthToken(String token) {
    List<String> authTokens = new ArrayList<String>(getAuthTokens());
    if (!authTokens.contains(authTokens)) {
      authTokens.add(token);      
    }
    if (authTokens.size() > MAX_TOKENS_COUNT) {
      authTokens.remove(0);
    }
    
    storeAuthTokens(authTokens);
  }
  
  private synchronized void storeAuthTokens(List<String> authTokens) {
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
    } finally {
      invalidateCache(); 
    }
  }

  private synchronized List<String> getAuthTokens() {
    if (tokensCache == null) {
      tokensCache = getPersistedAuthTokens();
    }
    
    return tokensCache;
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
    return getAuthTokens().contains(token);
  }
  
}
