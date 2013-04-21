package codeOrchestra.http;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;

import codeOrchestra.utils.PathUtils;

/**
 * @author Alexander Eliseyev
 */
public class CodeOrchestraHttpServer {

  public static final int PORT = 8091;
  
  private static CodeOrchestraHttpServer instance = new CodeOrchestraHttpServer();
  
  public static CodeOrchestraHttpServer getInstance() {
    return instance;
  }

  private Server server;
  private HandlerList activeHandlers;

  private Map<String, Handler> handlersMap = new HashMap<String, Handler>();

  private boolean mustReload;
  private long lastReloadRequest;
  private Object reloadMonitor = new Object();

  public void init() {
    server = new Server(PORT); // TODO: make configurable

    activeHandlers = new HandlerList();
    server.setHandler(activeHandlers);

    addAlias(PathUtils.getApplicationBaseDir(), "/");

    try {
      server.start();
    } catch (Exception e) {
      throw new RuntimeException("Can't start jetty server", e);
    }

    new ServerReloadThread().start();
  }

  public void addAlias(File baseDir, String alias) {
    Handler handler = getContextHandler(alias, getResourceHandler(baseDir.getPath() + "/"));
    addHandler(handler, alias);
  }

  private void addHandler(Handler handler, String alias) {
    Handler existingHandler = handlersMap.get(alias);
    if (existingHandler != null) {
      activeHandlers.removeHandler(existingHandler);
    }

    activeHandlers.addHandler(handler);
    handlersMap.put(alias, handler);

    reloadServer();
  }

  private void reloadServer() {
    synchronized (reloadMonitor) {
      mustReload = true;
      lastReloadRequest = System.currentTimeMillis();
    }
  }

  public void dispose() {
    try {
      server.stop();
    } catch (Exception e) {
      throw new RuntimeException("Can't stop HTTP server", e);
    }
  }

  private static ContextHandler getContextHandler(String contextPath, Handler handler) {
    ContextHandler contextHandler = new ContextHandler();
    contextHandler.setContextPath(contextPath);
    contextHandler.addHandler(handler);
    return contextHandler;
  }

  private static ResourceHandler getResourceHandler(String resourceBase) {
    ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setResourceBase(resourceBase);
    return resourceHandler;
  }

  private class ServerReloadThread extends Thread {
    private ServerReloadThread() {
      super("HTTP Server Reload Thread");
    }

    private void doReload() {
      try {
        server.stop();
        server.start();
      } catch (Exception e) {
        throw new RuntimeException("Can't reload jetty server", e);
      } finally {
        mustReload = false;
      }
    }

    @Override
    public void run() {
      while (true) {
        synchronized (reloadMonitor) {
          if (mustReload && (System.currentTimeMillis() - lastReloadRequest) > 1500) {
            doReload();
          }
        }

        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          // ignore
        }
      }
    }
  }

  
}
