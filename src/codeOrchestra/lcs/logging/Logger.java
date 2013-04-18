package codeOrchestra.lcs.logging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codeOrchestra.actionScript.logging.model.MessageScope;
import codeOrchestra.actionScript.logging.scope.MessageScopeRegistry;
import codeOrchestra.lcs.messages.MessagesManager;
import codeOrchestra.lcs.views.MessagesView;

/**
 * @author Alexander Eliseyev
 */
public final class Logger {

  private static final List<String> DEFAULT_SCOPES = new ArrayList<String>() {{
    add("0");
  }};
  private static Map<String, Logger> pool = new HashMap<String, Logger>();

  public static Logger getLogger(Class clazz) {
    return getLogger(clazz.getSimpleName());
  }
  
  public synchronized static Logger getLogger(String name) {
    if (pool.containsKey(name)) {
      return pool.get(name);
    }
    Logger logger = new Logger(name);
    pool.put(name, logger);
    return logger;
  }

  private String name;

  private Logger(String name) {
    this.name = name;
  }

  public void info(String message, List<String> scopeIds, long timestamp) {
    log(message, scopeIds, timestamp, Level.INFO);
  }

  public void warning(String message, List<String> scopeIds, long timestamp) {
    log(message, scopeIds, timestamp, Level.WARN);
  }

  public void error(String message, List<String> scopeIds, long timestamp) {
    log(message, scopeIds, timestamp, Level.ERROR);
  }
  
  public void error(String message) {
    log(message, DEFAULT_SCOPES, System.currentTimeMillis(), Level.ERROR);
  }
  
  public void error(Throwable t) {
    log(t.getMessage(), DEFAULT_SCOPES, System.currentTimeMillis(), Level.ERROR);
  }
  
  public void info(String message) {
    log(message, DEFAULT_SCOPES, System.currentTimeMillis(), Level.INFO);
  }
  
  public void info(Throwable t) {
    log(t.getMessage(), DEFAULT_SCOPES, System.currentTimeMillis(), Level.INFO);    
  }
  
  public void warning(String message) {
    log(message, DEFAULT_SCOPES, System.currentTimeMillis(), Level.WARN);
  }
  
  public void debug(Throwable t) {
    log(t.getMessage(), DEFAULT_SCOPES, System.currentTimeMillis(), Level.DEBUG);    
  }
  
  
  public void debug(String message) {
    log(message, DEFAULT_SCOPES, System.currentTimeMillis(), Level.DEBUG);   
  }
  
  private void log(String message, List<String> scopeIds, long timestamp, Level level) {
    for (String scopeId : scopeIds) {
      MessageScope scope = MessageScopeRegistry.getInstance().getScope(scopeId);
      if (scope == null) {
        continue;
      }
      
      MessagesView messageView = MessagesManager.getInstance().getViewByName(scope.getName());
      if (messageView == null) {
        continue;
      }
      
      messageView.addMessage(name, level, message, timestamp);
    }
  }

  public void assertTrue(boolean condition, String message) {
    if (!condition) {
      error(message);
    }
  }

    
}
