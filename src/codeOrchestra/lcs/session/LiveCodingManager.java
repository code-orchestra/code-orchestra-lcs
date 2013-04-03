package codeOrchestra.lcs.session;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import codeOrchestra.actionScript.modulemaker.MakeException;
import codeOrchestra.lcs.make.LCSMaker;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.socket.ClientSocketHandler;
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
  
  private Object monitor = new Object();

  private SourcesTrackerCallback sourcesTrackerCallback = new SourcesTrackerCallback() {
    @Override
    public void sourceFileChanged(SourceFile sourceFile) {
      reportChangedFile(sourceFile);
    }
  };

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
      synchronized (monitor) {
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
    synchronized (monitor) {
      changedFiles.add(sourceFile);      
    }
    
    tryRunIncrementalCompilation();    
  }

  private void tryRunIncrementalCompilation() {
    synchronized (monitor) {
      if (changedFiles.isEmpty()) {
        return;
      }      
    }
    
    if (!compilationInProgress) {
      runIncrementalCompilation();
    }
  }

  public void startSession(String sessionId, ClientSocketHandler clientSocketHandler) {
    stopSession();

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
  }

  public void stopSession() {
    if (sourceTrackerThread != null) {
      sourceTrackerThread.stopRightThere();
      sourceTrackerThread = null;
    }
  }

}
