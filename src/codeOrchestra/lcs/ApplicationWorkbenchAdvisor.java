package codeOrchestra.lcs;

import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * @author Alexander Eliseyev
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

  public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
    return new ApplicationWorkbenchWindowAdvisor(configurer);
  }

  public String getInitialWindowPerspectiveId() {
    return Perspective.ID;
  }

}
