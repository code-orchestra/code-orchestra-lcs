package codeOrchestra.lcs.session;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	}
  }; 
  
  public void startSession(String sessionId, ClientSocketHandler clientSocketHandler) {
	  // Start listening for source changes
	  List<File> sourceDirs = new ArrayList<File>();
	  for (String sourceDirPath : LCSProject.getCurrentProject().getSourceSettings().getSourcePaths()) {
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
