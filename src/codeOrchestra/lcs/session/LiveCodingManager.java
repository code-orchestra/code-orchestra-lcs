package codeOrchestra.lcs.session;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codeOrchestra.actionScript.liveCoding.LiveCodingSession;
import codeOrchestra.actionScript.liveCoding.listener.LiveCodingAdapter;
import codeOrchestra.actionScript.liveCoding.listener.LiveCodingListener;
import codeOrchestra.actionScript.liveCoding.run.LiveCodingSessionImpl;
import codeOrchestra.actionScript.modulemaker.MakeException;
import codeOrchestra.http.CodeOrchestraHttpServer;
import codeOrchestra.lcs.make.LCSMaker;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.run.Target;
import codeOrchestra.lcs.socket.ClientSocketHandler;
import codeOrchestra.lcs.socket.command.impl.PongTraceCommand;
import codeOrchestra.lcs.socket.command.impl.PongTraceCommand.PongListener;
import codeOrchestra.lcs.sources.SourceFile;
import codeOrchestra.lcs.sources.SourcesTrackerCallback;
import codeOrchestra.lcs.sources.SourcesTrackerThread;
import codeOrchestra.utils.FileUtils;
import codeOrchestra.utils.LocalhostUtil;
import codeOrchestra.utils.PathUtils;
import codeOrchestra.utils.StringUtils;
import codeOrchestra.utils.TemplateCopyUtil;
import codeOrchestra.utils.UnzipUtil;

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

  private List<String> deliveryMessages = new ArrayList<String>();

  private SourcesTrackerCallback sourcesTrackerCallback = new SourcesTrackerCallback() {
    @Override
    public void sourceFileChanged(SourceFile sourceFile) {
      reportChangedFile(sourceFile);
    }
  };

  public LiveCodingManager() {
    addListener(finisherThreadLiveCodingListener);
  }

  public void addDeliveryMessage(String deliveryMessage) {
    deliveryMessages.add(deliveryMessage);
  }

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

  public synchronized void runIncrementalCompilation() {
    LCSProject currentProject = LCSProject.getCurrentProject();
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
          // Extract and copy the artifact
          try {
            UnzipUtil.unzip(new File(PathUtils.getIncrementalSWCPath(currentProject)), FileUtils.getTempDir());
          } catch (IOException e) {
            // TODO: handle this nicely
            e.printStackTrace();
          }

          File extractedSWF = new File(FileUtils.getTempDir(), "library.swf");
          if (extractedSWF.exists()) {
            File artifact = new File(PathUtils.getIncrementalSWFPath(currentProject, getCurrentSession().getPackageNumber()));
            try {
              FileUtils.copyFileChecked(extractedSWF, artifact, false);
            } catch (IOException e) {
              // TODO: handle this nicely
              e.printStackTrace();
            }

            for (String deliveryMessage : deliveryMessages) {
              if (StringUtils.isNotEmpty(deliveryMessage)) {
                getCurrentSession().sendLiveCodingMessage(deliveryMessage);
              }
            }
            deliveryMessages.clear();

            getCurrentSession().incrementPackageNumber();

            // TODO:!
            // LiveCodingManager.instance().fireArtifactEvent(artifact.getPath());
          }

          tryRunIncrementalCompilation();
        }
      } catch (MakeException e) {
        // TODO: handle this nicely
        e.printStackTrace();
      }
    } finally {
      compilationInProgress = false;
    }
  }

  private void reportChangedFile(SourceFile sourceFile) {
    if (sourceFile.isAsset()) {
       tryUpdateAsset(sourceFile);
       return;
    }
    if (!sourceFile.isCompilable()) {
      return;
    }
    
    synchronized (runMonitor) {
      changedFiles.add(sourceFile);
    }

    tryRunIncrementalCompilation();
  }

  private synchronized void tryUpdateAsset(SourceFile assetFile) {
    long timeStamp = System.currentTimeMillis();

    // 0 - clear the incremental sources dir
    LCSProject currentProject = LCSProject.getCurrentProject();
    FileUtils.clear(currentProject.getOrCreateIncrementalSourcesDir());
 
    // 1 - copy/modify the source template file
    String classPostfix = assetFile.getFile().getName().replace(".", "_").replace(" ", "_") + timeStamp;
    String className = "Asset_" + classPostfix;
    File templateFile = new File(PathUtils.getTemplaesDir(), "Asset_update_template.as");
    File targetFile = new File(currentProject.getOrCreateIncrementalSourcesDir(), "codeOrchestra/liveCoding/load/" + className + ".as");
    Map<String, String> replacements = new HashMap<String, String>();
    replacements.put("{CLASS_POSTFIX}", classPostfix);
    replacements.put("{RELATIVE_PATH}", "../../../" + assetFile.getRelativePath());
    try {
      TemplateCopyUtil.copy(templateFile, targetFile, replacements);
    } catch (IOException e) {
      throw new RuntimeException("Can't copy the asset update source file: " + templateFile.getPath(), e);
    }
    
    // 2 - compile
    LCSMaker lcsMaker = new LCSMaker(Collections.singletonList(new SourceFile(targetFile, currentProject.getOrCreateIncrementalSourcesDir().getPath())), true);
    try {
      if (lcsMaker.make()) {
        // Extract and copy the artifact
        try {
          UnzipUtil.unzip(new File(PathUtils.getIncrementalSWCPath(currentProject)), FileUtils.getTempDir());
        } catch (IOException e) {
          // TODO: handle this nicely
          e.printStackTrace();
        }

        // 3 - copy the incremental swf
        File extractedSWF = new File(FileUtils.getTempDir(), "library.swf");
        if (extractedSWF.exists()) {
          File artifact = new File(PathUtils.getIncrementalSWFPath(currentProject, getCurrentSession().getPackageNumber()));
          try {
            FileUtils.copyFileChecked(extractedSWF, artifact, false);
          } catch (IOException e) {
            // TODO: handle this nicely
            e.printStackTrace();
          }

          // 4 - send the message
          StringBuilder sb = new StringBuilder("asset");
          sb.append(":").append("codeOrchestra.liveCoding.load.").append(className).append(":");
          sb.append(assetFile.getRelativePath()).append(":").append(timeStamp);
          currentSession.sendLiveCodingMessage(sb.toString());
          
          getCurrentSession().incrementPackageNumber();
        }
      }
    } catch (MakeException e) {
      // TODO: handle this nicely
      e.printStackTrace();
    }    
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

  public void sendBaseUrl(String baseUrl) {
    currentSession.sendLiveCodingMessage("base-url:" + baseUrl);
  }

  public void stopSession() {
    currentSession = null;

    if (sourceTrackerThread != null) {
      sourceTrackerThread.stopRightThere();
      sourceTrackerThread = null;
    }

    fireSessionEnd();
  }
  
  public String getWebOutputAddress() {
    return "http://" + LocalhostUtil.getLocalhostIp() + ":" + CodeOrchestraHttpServer.PORT + "/output";
  }

  private class SessionHandleListener extends LiveCodingAdapter {

    private SessionFinisher sessionFinisherThread;

    @Override
    public void onSessionStart(LiveCodingSession session) {
      // Clear livecoding output folder
      File incrementalDir = new File(PathUtils.getIncrementalOutputDir(LCSProject.getCurrentProject()));
      if (incrementalDir.exists()) {
        FileUtils.clear(incrementalDir);
      }

      if (sessionFinisherThread != null) {
        sessionFinisherThread.stopRightThere();
      }
      sessionFinisherThread = new SessionFinisher();
      sessionFinisherThread.start();

      if (LCSProject.getCurrentProject().getLiveCodingSettings().getLaunchTarget() != Target.SWF) {
        sendBaseUrl(getWebOutputAddress());
      }
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

    public static final int PING_TIMEOUT = 20000;
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
