package codeOrchestra.lcs.session;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import codeOrchestra.actionScript.liveCoding.LiveCodingSession;
import codeOrchestra.actionScript.liveCoding.listener.LiveCodingAdapter;
import codeOrchestra.actionScript.liveCoding.listener.LiveCodingListener;
import codeOrchestra.actionScript.liveCoding.run.LiveCodingSessionImpl;
import codeOrchestra.actionScript.modulemaker.MakeException;
import codeOrchestra.lcs.make.LCSMaker;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.socket.ClientSocketHandler;
import codeOrchestra.lcs.socket.command.impl.PongTraceCommand;
import codeOrchestra.lcs.socket.command.impl.PongTraceCommand.PongListener;
import codeOrchestra.lcs.sources.SourceFile;
import codeOrchestra.lcs.sources.SourcesTrackerCallback;
import codeOrchestra.lcs.sources.SourcesTrackerThread;

/**
 * @author Alexander Eliseyev
 */
public class LiveCodingManager {

  private static final LiveCodingManager instance = new LiveCodingManager();

  public static LiveCodingManager instance() {
    return instance;
  }

  private SourcesTrackerThread sourceTrackerThread;
  private boolean compilationInProgress;
  private List<SourceFile> changedFiles = new ArrayList<SourceFile>();
  
  private List<LiveCodingListener> liveCodingListeners = new ArrayList<LiveCodingListener>();
  
  private LiveCodingSession currentSession;
  
  private LiveCodingListener finisherThreadLiveCodingListener = new SessionHandleListener();
  
  private Object runMonitor = new Object();
  private Object listenerMonitor = new Object();

  public LiveCodingManager() {
    addListener(finisherThreadLiveCodingListener);
  }
  
  private SourcesTrackerCallback sourcesTrackerCallback = new SourcesTrackerCallback() {
    @Override
    public void sourceFileChanged(SourceFile sourceFile) {
      reportChangedFile(sourceFile);
    }
  };

  public void addListener(LiveCodingListener listener) {
    synchronized (listenerMonitor) {
      liveCodingListeners.add(listener);
    }
  }

  public void removeListener(LiveCodingListener listener) {
    synchronized (listenerMonitor) {
      liveCodingListeners.remove(listener);
    }
  }
  
  private void fireSessionStart() {
    synchronized (listenerMonitor) {
      for (LiveCodingListener listener : liveCodingListeners) {
        listener.onSessionStart(currentSession);
      }
    }
  }

  private void fireSessionEnd() {
    synchronized (listenerMonitor) {
      for (LiveCodingListener listener : liveCodingListeners) {
        listener.onSessionEnd(currentSession);
      }
    }
  }
  
  public boolean runBaseCompilation() {
    try {
      compilationInProgress = true;

      LCSMaker lcsMaker = new LCSMaker(false);
      try {
        return lcsMaker.make();
      } catch (MakeException e) {
        // TODO: handle this nicely
      }
    } finally {
      compilationInProgress = false;
    }
    return false;
  }

  public void runIncrementalCompilation() {
    try {
      compilationInProgress = true;

      List<SourceFile> changedFilesSnapshot;
      synchronized (runMonitor) {
        changedFilesSnapshot = new ArrayList<SourceFile>(changedFiles);
        changedFiles.clear();
      }
      
      LCSMaker lcsMaker = new LCSMaker(changedFilesSnapshot);
      try {
        if (lcsMaker.make()) {
          tryRunIncrementalCompilation();
        }
      } catch (MakeException e) {
        // TODO: handle this nicely
      }
    } finally {
      compilationInProgress = false;
    }
  }

  private void reportChangedFile(SourceFile sourceFile) {
    synchronized (runMonitor) {
      changedFiles.add(sourceFile);      
    }
    
    tryRunIncrementalCompilation();    
  }

  private void tryRunIncrementalCompilation() {
    synchronized (runMonitor) {
      if (changedFiles.isEmpty()) {
        return;
      }      
    }
    
    if (!compilationInProgress) {
      runIncrementalCompilation();
    }
  }

  public LiveCodingSession getCurrentSession() {
    return currentSession;
  }

  public void startSession(String sessionId, ClientSocketHandler clientSocketHandler) {
    stopSession();

    // Save session object
    currentSession = new LiveCodingSessionImpl(sessionId, System.currentTimeMillis(), clientSocketHandler);
    
    // Start listening for source changes
    List<File> sourceDirs = new ArrayList<File>();
    LCSProject currentProject = LCSProject.getCurrentProject();
    for (String sourceDirPath : currentProject.getSourceSettings().getSourcePaths()) {
      File sourceDir = new File(sourceDirPath);
      if (sourceDir.exists() && sourceDir.isDirectory()) {
        sourceDirs.add(sourceDir);
      }
    }

    sourceTrackerThread = new SourcesTrackerThread(sourcesTrackerCallback, sourceDirs);
    sourceTrackerThread.start();
    
    fireSessionStart();
  }

  public void stopSession() {
    currentSession = null;
    
    if (sourceTrackerThread != null) {
      sourceTrackerThread.stopRightThere();
      sourceTrackerThread = null;
    }
    
    fireSessionEnd();
  }
  
  private class SessionHandleListener extends LiveCodingAdapter {

    private SessionFinisher sessionFinisherThread;

    @Override
    public void onSessionStart(LiveCodingSession session) {
      if (sessionFinisherThread != null) {
        sessionFinisherThread.stopRightThere();
      }
      sessionFinisherThread = new SessionFinisher();
      sessionFinisherThread.start();
    }

    @Override
    public void onSessionEnd(LiveCodingSession session) {
      if (sessionFinisherThread != null) {
        sessionFinisherThread.stopRightThere();
        sessionFinisherThread = null;
      }
    }
  }
  
  private class SessionFinisher extends Thread implements PongListener {

    public static final int PING_TIMEOUT = 2000;
    public static final String PING_COMMAND = "ping";

    private boolean stop;

    private long lastPing;
    private long lastPong;

    public SessionFinisher() {
      PongTraceCommand.getInstance().addPongListener(this);
    }

    public void stopRightThere() {
      this.stop = true;
      PongTraceCommand.getInstance().removePongListener(this);
    }

    private void ping() {
      assert currentSession != null;
      lastPing = System.currentTimeMillis();
      currentSession.getSocketWriter().writeToSocket(PING_COMMAND);
    }

    @Override
    public void pong() {
      lastPong = System.currentTimeMillis();
    }

    @Override
    public void run() {
      while (!stop) {
        if (currentSession == null) {
          continue;
        }

        ping();

        try {
          Thread.sleep(PING_TIMEOUT);
        } catch (InterruptedException e) {
          // do nothing
        }

        if (lastPong < lastPing) {
          stopSession();
        }
      }
    }
  }

}
