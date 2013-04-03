package codeOrchestra.lcs.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessHandler;

import codeOrchestra.lcs.ICommandIds;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.run.LiveLauncher;
import codeOrchestra.lcs.session.LiveCodingManager;

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
    // 1 - Base compilation
    boolean successfulBaseGeneration = LiveCodingManager.instance().runBaseCompilation();    
        
    // 2 - Start the compiled SWF
    if (successfulBaseGeneration) {
      try {
        ProcessHandler processHandler = new LiveLauncher().launch(LCSProject.getCurrentProject());
//        processHandler.s
      } catch (ExecutionException e) {
        // TODO: handle nicely
        e.printStackTrace();
      }
    }
  }
  
}
