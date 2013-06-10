package codeOrchestra.lcs.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;

import codeOrchestra.actionScript.modulemaker.CompilationResult;
import codeOrchestra.lcs.ICommandIds;
import codeOrchestra.lcs.controller.COLTController;
import codeOrchestra.lcs.controller.COLTControllerCallback;
import codeOrchestra.lcs.project.LCSProject;

public class ProductionRunAction extends Action {

  private final IWorkbenchWindow window;

  public ProductionRunAction(IWorkbenchWindow window) {
    this.window = window;
    setText("Build and Run for Production");
    setId(ICommandIds.CMD_PRODUCTION_BUILD);
    setActionDefinitionId(ICommandIds.CMD_PRODUCTION_BUILD);
    setImageDescriptor(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/make.png"));

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
    setEnabled(false);
    
    COLTController.startProductionCompilation(window, new COLTControllerCallback<CompilationResult, CompilationResult>() {      
      @Override
      public void onError(Throwable t, CompilationResult errorResult) {
        setEnabled(true);        
      }
      
      @Override
      public void onComplete(CompilationResult successResult) {
        setEnabled(true);        
      }      
    }, true, false);
  }

}
