package codeOrchestra.lcs.project;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import codeOrchestra.lcs.config.view.LiveCodingProjectViews;

/**
 * @author Alexander Eliseyev
 */
public class ProjectManager {
  
  private final static ProjectManager instance = new ProjectManager();
  public static ProjectManager getInstance() {
    return instance;
  }
  
  public boolean openProject(String projectPath, IWorkbenchWindow window) throws PartInitException {
    // Close previous project
    LCSProject currentProject = LCSProject.getCurrentProject();
    if (currentProject != null) {
      currentProject.setDisposed();
    }
    if (!LiveCodingProjectViews.closeProjectViews()) {
      return false;
    }
    
    LCSProject newProject = LCSProject.loadFrom(projectPath);
    LiveCodingProjectViews.openProjectViews(window, newProject);
    
    return true;
  }

}
