package codeOrchestra.lcs.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import codeOrchestra.lcs.config.view.LiveCodingProjectViews;
import codeOrchestra.lcs.config.view.NameInputValidator;
import codeOrchestra.lcs.project.LCSProject;

/**
 * @author Alexander Eliseyev
 */
public class NewLiveCodingProjectAction extends Action {

  private IWorkbenchWindow window;

  public NewLiveCodingProjectAction(IWorkbenchWindow window) {
    this.window = window;
  }

  @Override
  public void run() {
    if (window != null) {
      try {
        // Project name
        String projectName = null;
        InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(), "New Live Coding Project", "Enter a project name", "", new NameInputValidator("Project"));
        if (dlg.open() == Window.OK) {
          projectName = dlg.getValue();
        } else {
          return;
        }

        // Project path
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        FileDialog dialog = new FileDialog(shell, SWT.SAVE);
        dialog.setFilterExtensions(new String[] { "*.lcp" });
        dialog.setFilterNames(new String[] { "Live Coding Project" });
        String projectPath = dialog.open();

        if (projectPath == null) {
          return;
        }
        
        LiveCodingProjectViews.closeAllViews();
        
        LCSProject newProject = LCSProject.createNew(projectName, projectPath);
        LiveCodingProjectViews.openViews(newProject);        
      } catch (PartInitException e) {
        MessageDialog.openError(window.getShell(), "Error", "Error opening view:" + e.getMessage());
      }
    }
  }

}
