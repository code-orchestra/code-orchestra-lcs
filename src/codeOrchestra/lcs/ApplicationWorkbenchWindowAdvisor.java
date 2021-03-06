package codeOrchestra.lcs;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * @author Alexander Eliseyev
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public static ApplicationWorkbenchWindowAdvisor sharedInstance;
  
    private IWorkbenchWindowConfigurer configurer;
    
    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
        sharedInstance = this;
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    @Override
    public void preWindowOpen() {
        configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(1000, 840));
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(true);
        configurer.setShowProgressIndicator(true);
    }
    
    @Override
    public void postWindowCreate() {
        super.postWindowCreate();
        configurer = getWindowConfigurer();
    }

    public void setTitle(String title) {
      configurer.setTitle(title);
    }
    
    @Override
    public boolean isDurableFolder(String perspectiveId, String folderId) {
      return Perspective.SETTINGS_FOLDER_ID.equals(folderId);
    }
    
}
