package codeOrchestra.lcs.logging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Eliseyev
 */
public final class Logger {

  private static Map<String, Logger> pool = new HashMap<String, Logger>();
  
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
    // TODO implement
    
  }

  public void warning(String message, List<String> scopeIds, long timestamp) {
    // TODO implement
    
  }

  public void error(String message, List<String> scopeIds, long timestamp) {
    // TODO implement
    
  }
  
}
