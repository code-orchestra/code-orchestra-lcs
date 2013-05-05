package codeOrchestra.lcs;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import codeOrchestra.lcs.errorhandling.ErrorHandler;
import codeOrchestra.lcs.project.ProjectManager;

/**
 * @author Alexander Eliseyev
 */
public class OpenDocumentEventProcessor implements Listener {	
  
	public void handleEvent(Event event) {
		if (event.text != null) {
      try {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (ProjectManager.getInstance().openProject(event.text, window)) {
          return; 
        } else {
          ApplicationWorkbenchAdvisor.pathToOpenOnStartup = event.text;
        }
      } catch (PartInitException e) {
        ErrorHandler.handle(e, "Error while opening COLT project: " + event.text);
      }        		  
		}
	}

}