package codeOrchestra.lcs.actions;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import codeOrchestra.lcs.ICommandIds;
import codeOrchestra.lcs.config.view.LiveCodingProjectViews;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.utils.NameInputValidator;

/**
 * @author Alexander Eliseyev
 */
public class NewProjectAction extends Action {

  private IWorkbenchWindow window;

  public NewProjectAction(IWorkbenchWindow window) {
    this.window = window;
    
    setText("New Project");
    // The id is used to refer to the action in a menu or toolbar
    setId(ICommandIds.CMD_NEW_PROJECT);
    // Associate the action with a pre-defined command, to allow key
    // bindings.
    setActionDefinitionId(ICommandIds.CMD_NEW_PROJECT);
    setImageDescriptor(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/new.gif"));
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
        dialog.setFileName(projectName);
        dialog.setFilterExtensions(new String[] { "*.colt" });
        dialog.setFilterNames(new String[] { "Live Coding Project" });
        String projectPath = dialog.open();

        if (projectPath == null) {
          return;
        }
        
        File projectFile = new File(projectPath);
        if (projectFile.exists()) {
          projectFile.delete();
        }
        projectFile.createNewFile();

        LCSProject currentProject = LCSProject.getCurrentProject();
        if (currentProject != null) {
          currentProject.setDisposed();
        }
        LiveCodingProjectViews.closeProjectViews();
        
        LCSProject newProject = LCSProject.createNew(projectName, projectPath);
        LiveCodingProjectViews.openProjectViews(window, newProject);        
      } catch (Throwable e) {
        MessageDialog.openError(window.getShell(), "Error", "Error opening view:" + e.getMessage());
        e.printStackTrace();
      }
    }
  }

}
