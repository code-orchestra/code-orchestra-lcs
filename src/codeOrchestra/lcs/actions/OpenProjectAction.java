package codeOrchestra.lcs.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import codeOrchestra.lcs.ICommandIds;
import codeOrchestra.lcs.config.view.LiveCodingProjectViews;
import codeOrchestra.lcs.project.LCSProject;

/**
 * @author Alexander Eliseyev
 */
public class OpenProjectAction extends Action {

  private final IWorkbenchWindow window;

  public OpenProjectAction(IWorkbenchWindow window, String label) {
    this.window = window;
    setText(label);
    setId(ICommandIds.CMD_OPEN);
    setActionDefinitionId(ICommandIds.CMD_OPEN);
    setImageDescriptor(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/open.gif"));
  }

  @Override
  public void run() {
    if (window != null) {
      try {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

        FileDialog dialog = new FileDialog(shell, SWT.OPEN);
        dialog.setFilterExtensions(new String[] { "*.lcp" });
        dialog.setFilterNames(new String[] { "Live Coding Project" });
        String fileSelected = dialog.open();

        if (fileSelected == null) {
          return;
        }

        // Close previous project
        LiveCodingProjectViews.closeProjectViews();

        LCSProject newProject = LCSProject.loadFrom(fileSelected);
        LiveCodingProjectViews.openProjectViews(window, newProject);
      } catch (PartInitException e) {
        MessageDialog.openError(window.getShell(), "Error", "Error opening view:" + e.getMessage());
      }
    }
  }

}
