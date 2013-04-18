package codeOrchestra.lcs.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;

import codeOrchestra.actionScript.compiler.fcsh.FCSHException;
import codeOrchestra.actionScript.compiler.fcsh.FCSHManager;
import codeOrchestra.lcs.ICommandIds;
import codeOrchestra.lcs.config.view.LiveCodingProjectViews;
import codeOrchestra.lcs.digest.ProjectDigestHelper;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.run.LiveLauncher;
import codeOrchestra.lcs.session.LiveCodingManager;

import com.intellij.execution.ExecutionException;

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
    // Save project
    LCSProject currentProject = LCSProject.getCurrentProject();
    LiveCodingProjectViews.saveProjectViewsState(window, currentProject);   
    currentProject.save();
    
    // Validate project 
    if (!LiveCodingProjectViews.validateProjectViewsState(window, currentProject)) {
      return;
    }
    
    // Build digests
    new ProjectDigestHelper(currentProject).build();
    
    // Restart FCSH
    try {
      FCSHManager.instance().restart();
    } catch (FCSHException e) {
      // TODO: handle nicely
      e.printStackTrace();
    }
    
    new Thread() {
      public void run() {
        // 1 - Base compilation
        boolean successfulBaseGeneration = LiveCodingManager.instance().runBaseCompilation();    
            
        // 2 - Start the compiled SWF
        if (successfulBaseGeneration) {
          try {
            new LiveLauncher().launch(LCSProject.getCurrentProject());
          } catch (ExecutionException e) {
            // TODO: handle nicely
            e.printStackTrace();
          }
        }
      };
    }.start();    
  }
  
}
