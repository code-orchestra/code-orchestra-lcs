package codeOrchestra.lcs.session;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import codeOrchestra.actionScript.compiler.fcsh.FCSHException;
import codeOrchestra.actionScript.compiler.fcsh.FCSHManager;
import codeOrchestra.lcs.LCSException;
import codeOrchestra.lcs.flex.config.FlexConfig;
import codeOrchestra.lcs.flex.config.FlexConfigBuilder;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.socket.ClientSocketHandler;
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

  private SourcesTrackerCallback sourcesTrackerCallback = new SourcesTrackerCallback() {
    @Override
    public void sourceFileChanged(File sourceFile) {
      // TODO: implement
      System.out.println("File changed: " + sourceFile);
    }
  };

  public void startSession(String sessionId, ClientSocketHandler clientSocketHandler) {
    try {
      FCSHManager.instance().clear();
    } catch (FCSHException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    
    LCSProject currentProject = LCSProject.getCurrentProject();
    
    // Generate & save Flex config    
    FlexConfigBuilder flexConfigBuilder = new FlexConfigBuilder(currentProject, false, false);
    FlexConfig flexConfig = null;
    try {
      flexConfig = flexConfigBuilder.build();
    } catch (LCSException e) {
      // TODO: handle this
    }
    try {
      flexConfig.saveToFile(currentProject.getFlexConfigPath(currentProject));
    } catch (LCSException e) {
      // TODO: handle this
    }
    
    // Start listening for source changes
    List<File> sourceDirs = new ArrayList<File>();
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
