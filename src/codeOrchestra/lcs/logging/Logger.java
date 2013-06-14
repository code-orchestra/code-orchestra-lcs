package codeOrchestra.lcs.logging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

import codeOrchestra.actionScript.logging.model.MessageScope;
import codeOrchestra.actionScript.logging.scope.MessageScopeRegistry;
import codeOrchestra.lcs.Activator;
import codeOrchestra.lcs.messages.MessagesManager;
import codeOrchestra.lcs.session.SessionViewManager;
import codeOrchestra.lcs.views.MessagesView;
import codeOrchestra.lcs.views.SessionView;

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

  public void error(String message, List<String> scopeIds, long timestamp, String stackTrace) {
    log(message, scopeIds, timestamp, Level.ERROR, stackTrace);
  }
  
  public void error(String message) {
    log(message, DEFAULT_SCOPES, System.currentTimeMillis(), Level.ERROR);
  }
  
  public void error(Throwable t) {
    log(t.getMessage(), DEFAULT_SCOPES, System.currentTimeMillis(), Level.ERROR);
    
    IStatus status = new Status(IStatus.ERROR, "code-orchestra-lcs", 0, null, t);
    Platform.getLog(Activator.getDefault().getBundle()).log(status);
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
    log(message, scopeIds, timestamp, level, null);
  }
  
  private void log(String message, List<String> scopeIds, long timestamp, Level level, String stackTrace) {
    boolean hasLiveCodingScope = false;
    for (String scopeId : scopeIds) {
      if (scopeId.startsWith("livecoding")) {
        hasLiveCodingScope = true;
      }
    }
    if (hasLiveCodingScope) {
      level = Level.DEBUG;
      
      if (scopeIds.get(0).contains("_")) {
        String clientId = scopeIds.get(0).split("_")[1];
        SessionView sessionView = SessionViewManager.getInstance().getViewByName(clientId);
        if (sessionView == null) {
          return;
        }
        sessionView.addMessage(name, level, message, timestamp, stackTrace);
        return;
      }
    }
    
    for (String scopeId : scopeIds) {
      MessageScope scope = MessageScopeRegistry.getInstance().getScope(scopeId);
      if (scope == null) {
        continue;
      }
      
      if (scopeId.contains("_")) {
        String clientId = scopeId.split("_")[1];
        SessionView sessionView = SessionViewManager.getInstance().getViewByName(clientId);
        if (sessionView == null) {
          return;
        }
        sessionView.addMessage(name, level, message, timestamp, stackTrace);
        return;
      }
      
      MessagesView messageView = MessagesManager.getInstance().getViewByName(scope.getName());
      if (messageView == null) {
        continue;
      }      
      messageView.addMessage(name, level, message, timestamp, stackTrace);
    }
  }

  public void assertTrue(boolean condition, String message) {
    if (!condition) {
      error(message);
    }
  }

    
}
