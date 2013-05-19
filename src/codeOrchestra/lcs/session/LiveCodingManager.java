package codeOrchestra.lcs.session;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import codeOrchestra.actionScript.compiler.fcsh.FCSHException;
import codeOrchestra.actionScript.compiler.fcsh.FCSHManager;
import codeOrchestra.actionScript.liveCoding.LiveCodingSession;
import codeOrchestra.actionScript.liveCoding.listener.LiveCodingAdapter;
import codeOrchestra.actionScript.liveCoding.listener.LiveCodingListener;
import codeOrchestra.actionScript.liveCoding.run.LiveCodingSessionImpl;
import codeOrchestra.actionScript.modulemaker.MakeException;
import codeOrchestra.http.CodeOrchestraHttpServer;
import codeOrchestra.lcs.digest.EmbedDigest;
import codeOrchestra.lcs.errorhandling.ErrorHandler;
import codeOrchestra.lcs.logging.Logger;
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

  private static final Logger LOG = Logger.getLogger(LiveCodingManager.class);

  private static final LiveCodingManager instance = new LiveCodingManager();

  public static LiveCodingManager instance() {
    return instance;
  }

  private SourcesTrackerThread sourceTrackerThread;
  private boolean compilationInProgress;
  private List<SourceFile> changedFiles = new ArrayList<SourceFile>();

  private List<LiveCodingListener> liveCodingListeners = new ArrayList<LiveCodingListener>();

  private Map<String, LiveCodingSession> currentSessions = new HashMap<String, LiveCodingSession>();

  private LiveCodingListener finisherThreadLiveCodingListener = new SessionHandleListener();

  private Object runMonitor = new Object();
  private Object listenerMonitor = new Object();

  private List<String> deliveryMessages = new ArrayList<String>();
  private Map<String, List<String>> deliveryMessagesHistory = new HashMap<String, List<String>>();

  // full path -> list of embeds
  private Map<String, List<EmbedDigest>> embedDigests = new HashMap<String, List<EmbedDigest>>();

  private int packageId = 1;

  private SourcesTrackerCallback sourcesTrackerCallback = new SourcesTrackerCallback() {
    @Override
    public void sourceFileChanged(SourceFile sourceFile) {
      reportChangedFile(sourceFile);
    }
  };

  public LiveCodingManager() {
    addListener(finisherThreadLiveCodingListener);
  }

  public LiveCodingSession getSession(String clientId) {
    return currentSessions.get(clientId);
  }

  public void resetEmbeds(List<EmbedDigest> embeds) {
    embedDigests.clear();
    for (EmbedDigest embedDigest : embeds) {
      List<EmbedDigest> storedEmbeds = getEmbedDigests(embedDigest.getFullPath());
      if (storedEmbeds == null) {
        storedEmbeds = new ArrayList<EmbedDigest>();
        embedDigests.put(embedDigest.getFullPath(), storedEmbeds);
      }

      storedEmbeds.add(embedDigest);
    }
  }

  private List<EmbedDigest> getEmbedDigests(String fullPath) {
    return embedDigests.get(fullPath);
  }

  public void addDeliveryMessageToHistory(String broadcastId, String deliveryMessage) {
    List<String> history = deliveryMessagesHistory.get(broadcastId);
    if (history == null) {
      history = new ArrayList<String>();
      deliveryMessagesHistory.put(broadcastId, history);
    }
    history.add(deliveryMessage);
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

  private void fireSessionStart(LiveCodingSession session) {
    synchronized (listenerMonitor) {
      for (LiveCodingListener listener : liveCodingListeners) {
        listener.onSessionStart(session);
      }
    }
  }

  private void fireSessionEnd(LiveCodingSession session) {
    synchronized (listenerMonitor) {
      for (LiveCodingListener listener : liveCodingListeners) {
        listener.onSessionEnd(session);
      }
    }
  }

  public boolean runBaseCompilation() {
    try {
      compilationInProgress = true;

      LCSProject currentProject = LCSProject.getCurrentProject();
      FileUtils.clear(new File(PathUtils.getIncrementalOutputDir(currentProject)));

      // COLT-186
      String htmlTemplatePath = currentProject.getSourceSettings().getHTMLTemplatePath();
      if (StringUtils.isNotEmpty(htmlTemplatePath)) {
        File htmlTemplateDir = new File(htmlTemplatePath);
        if (htmlTemplateDir.exists() && htmlTemplateDir.isDirectory()) {
          FileUtils.copyDir(htmlTemplateDir, currentProject.getOutputDir(), false);
        }
      }

      LCSMaker lcsMaker = new LCSMaker(false);
      try {
        return lcsMaker.make();
      } catch (MakeException e) {
        ErrorHandler.handle(e, "Error while running base compilation");
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

      try {
        FCSHManager.instance().startCPUProfiling();
      } catch (FCSHException e1) {
        ErrorHandler.handle(e1, "Error while starting profling");
      }

      LCSMaker lcsMaker = new LCSMaker(changedFilesSnapshot);
      try {
        if (lcsMaker.make()) {
          try {
            FCSHManager.instance().stopCPUProfiling();
          } catch (FCSHException e) {
            ErrorHandler.handle(e, "Error while stopping profling");
          }

          // Copy the swc to the incremental dir
          try {
            FileUtils.copyFileChecked(new File(PathUtils.getSourceIncrementalSWCPath(currentProject)), new File(PathUtils.getTargetIncrementalSWCPath(currentProject, packageId)), false);
          } catch (IOException e) {
            ErrorHandler.handle(e, "Error while copying incremental compilation artifact");
          }

          // Extract and copy the artifact
          try {
            UnzipUtil.unzip(new File(PathUtils.getSourceIncrementalSWCPath(currentProject)), FileUtils.getTempDir());
          } catch (IOException e) {
            ErrorHandler.handle(e, "Error while unzipping incremental compilation artifact");
          }

          // Copy the swf from swc and copy to the incremental dir
          File extractedSWF = new File(FileUtils.getTempDir(), "library.swf");
          if (extractedSWF.exists()) {
            File artifact = new File(PathUtils.getIncrementalSWFPath(currentProject, packageId));
            try {
              FileUtils.copyFileChecked(extractedSWF, artifact, false);
            } catch (IOException e) {
              ErrorHandler.handle(e, "Error while copying incremental compilation artifact");
            }

            for (String deliveryMessage : deliveryMessages) {
              if (StringUtils.isNotEmpty(deliveryMessage)) {
                sendLiveCodingMessage(deliveryMessage);
              } else {
                LOG.debug("No updatable changes were made sine last compilation");
              }
            }
            deliveryMessages.clear();

            incrementPackageNumber();
          }

          tryRunIncrementalCompilation();
        }
      } catch (MakeException e) {
        ErrorHandler.handle(e, "Error while compiling");
      }
    } finally {
      compilationInProgress = false;
    }
  }

  private void incrementPackageNumber() {
    packageId++;
  }

  private void reportChangedFile(SourceFile sourceFile) {
    if (sourceFile.isAsset()) {
      List<EmbedDigest> embedDigestsByFullPath = embedDigests.get(sourceFile.getFile().getPath());

      Map<String, List<String>> mimeTypeToSourceAttributes = new HashMap<String, List<String>>();
      for (EmbedDigest embedDigest : embedDigestsByFullPath) {
        List<String> sourceAttributes = mimeTypeToSourceAttributes.get(embedDigest.getMimeType());
        if (sourceAttributes == null) {
          sourceAttributes = new ArrayList<String>();
          mimeTypeToSourceAttributes.put(embedDigest.getMimeType(), sourceAttributes);
        }

        if (!sourceAttributes.contains(embedDigest.getSource())) {
          sourceAttributes.add(embedDigest.getSource());
        }
      }

      for (String mimeType : mimeTypeToSourceAttributes.keySet()) {
        tryUpdateAsset(sourceFile, mimeType, mimeTypeToSourceAttributes.get(mimeType));
      }

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

  private synchronized void tryUpdateAsset(SourceFile assetFile, String mimeType, List<String> sourceAttributes) {
    long timeStamp = System.currentTimeMillis();

    // 0 - clear the incremental sources dir
    LCSProject currentProject = LCSProject.getCurrentProject();
    FileUtils.clear(currentProject.getOrCreateIncrementalSourcesDir());

    // 1 - copy the changed asset to the root of incremental dir
    try {
      FileUtils.copyFileChecked(assetFile.getFile(), new File(currentProject.getOrCreateIncrementalSourcesDir(), assetFile.getFile().getName()), false);
    } catch (IOException e) {
      throw new RuntimeException("Can't copy the asset file: " + assetFile.getFile().getPath(), e);
    }

    // 2 - copy/modify the source template file
    String classPostfix = assetFile.getFile().getName().replace(".", "_").replace(" ", "_") + timeStamp;
    String className = "Asset_" + classPostfix;
    File templateFile = new File(PathUtils.getTemplaesDir(), StringUtils.isEmpty(mimeType) ? "Asset_update_template.as" : "Asset_update_mimetype_template.as");
    File targetFile = new File(currentProject.getOrCreateIncrementalSourcesDir(), "codeOrchestra/liveCoding/load/" + className + ".as");

    Map<String, String> replacements = new HashMap<String, String>();
    replacements.put("{CLASS_POSTFIX}", classPostfix);
    replacements.put("{RELATIVE_PATH}", "/" + assetFile.getFile().getName());
    replacements.put("{MIME_TYPE}", mimeType);

    try {
      TemplateCopyUtil.copy(templateFile, targetFile, replacements);
    } catch (IOException e) {
      throw new RuntimeException("Can't copy the asset update source file: " + templateFile.getPath(), e);
    }

    // 3 - compile
    LCSMaker lcsMaker = new LCSMaker(Collections.singletonList(new SourceFile(targetFile, currentProject.getOrCreateIncrementalSourcesDir().getPath())), true);
    try {
      if (lcsMaker.make()) {
        // Extract and copy the artifact
        try {
          UnzipUtil.unzip(new File(PathUtils.getSourceIncrementalSWCPath(currentProject)), FileUtils.getTempDir());
        } catch (IOException e) {
          ErrorHandler.handle(e, "Error while unzipping incremental compilation artifact (asset)");
        }

        // 4 - copy the incremental swf
        File extractedSWF = new File(FileUtils.getTempDir(), "library.swf");
        if (extractedSWF.exists()) {
          File artifact = new File(PathUtils.getIncrementalSWFPath(currentProject, packageId));
          try {
            FileUtils.copyFileChecked(extractedSWF, artifact, false);
          } catch (IOException e) {
            ErrorHandler.handle(e, "Error while copying incremental compilation artifact (asset)");
          }

          // 5 - send the message
          for (String sourceAttribute : sourceAttributes) {
            StringBuilder sb = new StringBuilder("asset");
            sb.append(":").append("codeOrchestra.liveCoding.load.").append(className).append(":");
            sb.append(sourceAttribute).append(":");
            if (StringUtils.isEmpty(mimeType)) {
              sb.append(":");
            } else {
              sb.append(mimeType).append(":");
            }
            sb.append(timeStamp);

            sendLiveCodingMessage(sb.toString());

            try {
              Thread.sleep(60);
            } catch (InterruptedException e) {
            }
          }

          // 6 - increment package number
          incrementPackageNumber();
        }
      }
    } catch (MakeException e) {
      ErrorHandler.handle(e, "Error while compiling");
    }
  }

  public void sendLiveCodingMessage(String message) {
    for (LiveCodingSession liveCodingSession : currentSessions.values()) {
      liveCodingSession.sendLiveCodingMessage(message, String.valueOf(packageId), true);
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

  public void startSession(String broadcastId, String clientId, Map<String, String> clientInfo, ClientSocketHandler clientSocketHandler) {
    boolean noSessionsWereActive = currentSessions.isEmpty();

    LiveCodingSession newSession = new LiveCodingSessionImpl(broadcastId, clientId, clientInfo, System.currentTimeMillis(), clientSocketHandler);
    currentSessions.put(clientId, newSession);

    if (noSessionsWereActive) {
      resetPackageId();
      startListeningForSourcesChanges();
    }

    fireSessionStart(newSession);
  }

  public void startListeningForSourcesChanges() {
    List<File> watchedDirs = new ArrayList<File>();
    LCSProject currentProject = LCSProject.getCurrentProject();
    for (String sourceDirPath : currentProject.getSourceSettings().getSourcePaths()) {
      File sourceDir = new File(sourceDirPath);
      if (sourceDir.exists() && sourceDir.isDirectory()) {
        watchedDirs.add(sourceDir);
      }
    }
    for (String assetDirPath : currentProject.getSourceSettings().getAssetPaths()) {
      File assetDir = new File(assetDirPath);
      if (assetDir.exists() && assetDir.isDirectory()) {
        watchedDirs.add(assetDir);
      }
    }

    sourceTrackerThread = new SourcesTrackerThread(sourcesTrackerCallback, watchedDirs);
    sourceTrackerThread.start();
  }

  public void stopListeningForSourcesChanges() {
    if (sourceTrackerThread != null) {
      sourceTrackerThread.stopRightThere();
      sourceTrackerThread = null;
    }
  }

  public void sendBaseUrl(LiveCodingSession session, String baseUrl) {
    session.sendLiveCodingMessage("base-url:" + baseUrl, String.valueOf(packageId), false);
  }

  public String getWebOutputAddress() {
    return "http://" + LocalhostUtil.getLocalhostIp() + ":" + CodeOrchestraHttpServer.PORT + "/output";
  }

  private void restoreSessionState(LiveCodingSession session) {
    List<String> history = deliveryMessagesHistory.get(session.getBroadcastId());
    if (history != null) {
      for (String deliveryMessage : history) {
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          // ignore
        }
        session.sendMessageAsIs(deliveryMessage);
      }
    }
  }

  public void stopSession(LiveCodingSession liveCodingSession) {
    if (liveCodingSession.isDisposed()) {
      return;
    }

    liveCodingSession.dispose();

    currentSessions.remove(liveCodingSession.getClientId());

    if (currentSessions.isEmpty()) {
      resetPackageId();
      stopListeningForSourcesChanges();
    }

    fireSessionEnd(liveCodingSession);
  }

  public void resetPackageId() {
    packageId = 1;
  }

  public Set<String> getCurrentSessionsCliensIds() {
    return currentSessions.keySet();
  }

  private class SessionHandleListener extends LiveCodingAdapter {

    // clientId -> session finisher thread
    private Map<String, SessionFinisher> sessionFinisherThreads = new HashMap<String, LiveCodingManager.SessionFinisher>();

    @Override
    public void onSessionStart(LiveCodingSession session) {
      String clientId = session.getClientId();

      SessionFinisher sessionFinisherThread = getSessionFinisherThread(clientId);
      if (sessionFinisherThread == null) {
        sessionFinisherThread = new SessionFinisher(clientId);
        sessionFinisherThreads.put(clientId, sessionFinisherThread);
      } else {
        sessionFinisherThread.stopRightThere();
      }
      sessionFinisherThread.start();

      if (LCSProject.getCurrentProject().getLiveCodingSettings().getLaunchTarget() != Target.SWF) {
        sendBaseUrl(session, getWebOutputAddress());
      }

      restoreSessionState(session);

      LOG.info("Established a connection: broadcast ID: " + session.getBroadcastId() + ", cliend ID: " + clientId + ", client: " + session.getBasicClientInfo());
    }

    public SessionFinisher getSessionFinisherThread(String clientId) {
      return sessionFinisherThreads.get(clientId);
    }

    @Override
    public void onSessionEnd(LiveCodingSession session) {
      SessionFinisher sessionFinisherThread = getSessionFinisherThread(session.getClientId());
      if (sessionFinisherThread != null) {
        sessionFinisherThread.stopRightThere();
      }
    }
  }

  private class SessionFinisher extends Thread implements PongListener {

    public static final int PING_TIMEOUT = 3000;
    public static final String PING_COMMAND = "ping";

    private boolean stop;

    private long lastPing;
    private long lastPong;

    private final String clientId;

    public SessionFinisher(String clientId) {
      this.clientId = clientId;
    }

    public void stopRightThere() {
      this.stop = true;
      PongTraceCommand.getInstance().removePongListener(this);
    }

    private LiveCodingSession getLiveCodingSession() {
      return currentSessions.get(clientId);
    }

    private void ping() {
      lastPing = System.currentTimeMillis();
      getLiveCodingSession().getSocketWriter().writeToSocket(PING_COMMAND);
    }

    @Override
    public void pong() {
      lastPong = System.currentTimeMillis();
    }

    @Override
    public void run() {
      this.stop = false;
      PongTraceCommand.getInstance().addPongListener(this);

      while (!stop) {
        LiveCodingSession liveCodingSession = getLiveCodingSession();
        if (liveCodingSession == null) {
          continue;
        }

        ping();

        try {
          Thread.sleep(PING_TIMEOUT);
        } catch (InterruptedException e) {
          // do nothing
        }

        if (lastPong < lastPing) {
          if (LCSProject.getCurrentProject().getLiveCodingSettings().disconnectOnTimeout()) {
            stopSession(liveCodingSession);
          }
        }
      }
    }
  }

}
