package codeOrchestra.lcs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

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

}
