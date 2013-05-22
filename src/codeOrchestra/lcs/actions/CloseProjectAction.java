package codeOrchestra.lcs.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;

import codeOrchestra.lcs.ICommandIds;
import codeOrchestra.lcs.config.view.LiveCodingProjectViews;
import codeOrchestra.lcs.project.LCSProject;

/**
 * @author Alexander Eliseyev
 */
public class CloseProjectAction extends Action {

  private final IWorkbenchWindow window;

  public CloseProjectAction(IWorkbenchWindow window, String label) {
    this.window = window;
    
    setText(label);
    setId(ICommandIds.CMD_CLOSE);
    setActionDefinitionId(ICommandIds.CMD_CLOSE);
    
    setEnabled(false);
    
    window.addPerspectiveListener(new PerspectiveAdapter() {      
      @Override
      public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
        setEnabled(LCSProject.getCurrentProject() != null);
      }
    });
  }
  
  @Override
  public void run() {
    LiveCodingProjectViews.closeProjectViews();
    LCSProject.close();
  }
  
}
