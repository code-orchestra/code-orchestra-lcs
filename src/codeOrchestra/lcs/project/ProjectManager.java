package codeOrchestra.lcs.project;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import codeOrchestra.actionScript.compiler.fcsh.FCSHException;
import codeOrchestra.actionScript.compiler.fcsh.FCSHManager;
import codeOrchestra.lcs.config.view.LiveCodingProjectViews;

/**
 * @author Alexander Eliseyev
 */
public class ProjectManager {
  
  private final static ProjectManager instance = new ProjectManager();
  public static ProjectManager getInstance() {
    return instance;
  }
  
  public void openProject(String projectPath, IWorkbenchWindow window) throws PartInitException {
    // Close previous project
    LiveCodingProjectViews.closeProjectViews();
    
    // Clear fcsh targets
    FCSHManager.instance().clearTargets();
    
    /*
    try {
      FCSHManager.instance().deleteLivecodingCaches();
    } catch (FCSHException e) {
      // ignore
    }
    */

    LCSProject newProject = LCSProject.loadFrom(projectPath);
    LiveCodingProjectViews.openProjectViews(window, newProject);
  }

}
