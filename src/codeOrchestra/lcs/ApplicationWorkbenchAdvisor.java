package codeOrchestra.lcs;

import java.io.File;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import codeOrchestra.lcs.project.ProjectManager;
import codeOrchestra.lcs.project.RecentProjects;

/**
 * @author Alexander Eliseyev
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

  private Application application;

  public ApplicationWorkbenchAdvisor(Application application) {
    this.application = application;
  }

  public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
    return new ApplicationWorkbenchWindowAdvisor(configurer);
  }

  public String getInitialWindowPerspectiveId() {
    return Perspective.ID;
  }
  
  @Override
  public void initialize(IWorkbenchConfigurer configurer) {
    super.initialize(configurer);
    
    Throwable socketInitException = application.getServerSocketThread().getSocketInitException();
    if (socketInitException != null) {
      MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Can't init tracing socket: " + socketInitException);
    }
  }
  
  @Override
  public void postStartup() {
    List<String> recentProjectsPaths = RecentProjects.getRecentProjectsPaths();
    if (!recentProjectsPaths.isEmpty()) {
      String lastProjectPath = recentProjectsPaths.get(0);
      
      IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      if (new File(lastProjectPath).exists()) {
        try {
          ProjectManager.getInstance().openProject(lastProjectPath, window);
        } catch (PartInitException e) {
          // TODO: Handle nicely
          e.printStackTrace();
        }        
      }
    }
  }

}
