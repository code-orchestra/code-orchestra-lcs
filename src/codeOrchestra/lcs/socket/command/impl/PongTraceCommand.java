package codeOrchestra.lcs.socket.command.impl;

import codeOrchestra.actionScript.logging.model.LoggerMessage;
import codeOrchestra.lcs.socket.ClientSocketHandler;
import codeOrchestra.lcs.socket.command.TraceCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public final class PongTraceCommand implements TraceCommand {

  private static PongTraceCommand instance;
  public synchronized static PongTraceCommand getInstance() {
    if (instance == null) {
      instance = new PongTraceCommand();
    }
    return instance;
  }

  private static final String PONG_COMMAND = "pong";

  private List<PongListener> pongListeners = new ArrayList<PongListener>();

  private PongTraceCommand() {
  }

  @Override
  public boolean isApplicable(LoggerMessage message) {
    return PONG_COMMAND.equals(message.getCommand());
  }

  @Override
  public void execute(LoggerMessage message, ClientSocketHandler clientSocketHandler) {
    synchronized (this) {
      for (PongListener pongListener : pongListeners) {
        pongListener.pong();
      }
    }
  }

  public synchronized void addPongListener(PongListener listener) {
    pongListeners.add(listener);
  }

  public synchronized void removePongListener(PongListener listener) {
    pongListeners.remove(listener);
  }

  public static interface PongListener {
    void pong();
  }

}
