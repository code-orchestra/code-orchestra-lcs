package codeOrchestra.lcs.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;

import codeOrchestra.lcs.ICommandIds;
import codeOrchestra.lcs.LCSException;
import codeOrchestra.lcs.flex.config.FlexConfig;
import codeOrchestra.lcs.flex.config.FlexConfigBuilder;
import codeOrchestra.lcs.project.LCSProject;

public class StartSessionAction extends Action {

  private final IWorkbenchWindow window;

  public StartSessionAction(IWorkbenchWindow window) {
    this.window = window;
    setText("Start Live Coding Session");
    setId(ICommandIds.CMD_START_SESSION);
    setActionDefinitionId(ICommandIds.CMD_START_SESSION);
    setImageDescriptor(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/run.png"));
    
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
    // 1 - Generate Flex config
    FlexConfigBuilder flexConfigBuilder = new FlexConfigBuilder(LCSProject.getCurrentProject(), false);
    FlexConfig flexConfig;
    try {      
      flexConfig = flexConfigBuilder.build();
    } catch (LCSException e) {
      MessageDialog.openError(window.getShell(), "Error", "Error building Flex compiler config: " + e.getMessage());
      return;
    }    
    
    
    // TODO: implement further
  }
  
}
