package codeOrchestra.lcs.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
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
public class SaveProjectAction extends Action {
	
	private final IWorkbenchWindow window;

  public SaveProjectAction(IWorkbenchWindow window, String label) {
		this.window = window;
    setText(label);
		setId(ICommandIds.CMD_SAVE);
		setActionDefinitionId(ICommandIds.CMD_SAVE);
		setImageDescriptor(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/save.gif"));
		
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
	  new Job("Saving project") {
      @Override
      protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Saving project", 100);
        
        Display.getDefault().syncExec(new Runnable() {
          @Override
          public void run() {
            LCSProject currentProject = LCSProject.getCurrentProject();
            LiveCodingProjectViews.saveProjectViewsState(window, currentProject);   
            currentProject.save();            
          }
        });
        
        try {
          Thread.sleep(200);
        } catch (InterruptedException e) {
          // ignore
        }
        monitor.worked(100);
        
        return Status.OK_STATUS;
      }
	  }.schedule();
	}
	

}
