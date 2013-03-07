package codeOrchestra.lcs.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import codeOrchestra.lcs.Activator;
import codeOrchestra.lcs.ICommandIds;
import codeOrchestra.lcs.config.view.ConfigurationNameInputValidator;
import codeOrchestra.lcs.config.view.LiveConfigViewStack;

public class NewLiveCodingConfigurationAction extends Action {

	private final IWorkbenchWindow window;
	private final String viewId;

	public NewLiveCodingConfigurationAction(IWorkbenchWindow window, String label, String viewId) {
		this.window = window;
		this.viewId = viewId;
		setText(label);
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_NEW);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_NEW);
		setImageDescriptor(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/new.gif"));
	}

	public void run() {
		if (window != null) {
			try {
				// Configuration name
				String configurationName = null;
				InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(), "New Live Run Configuration", "Enter a configuration name", "", new ConfigurationNameInputValidator());
				if (dlg.open() == Window.OK) {
					configurationName = dlg.getValue();
					LiveConfigViewStack.lastName = configurationName;
				} else {
					return;
				}

				// Configuration path
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		        FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		        dialog.setFilterExtensions(new String[] {"*.lcc"});
		        dialog.setFilterNames(new String[] {"Live Coding Configuration"});
		        String fileSelected = dialog.open();
		
		        if (fileSelected != null) {
		          LiveConfigViewStack.lastPath = fileSelected;
		        } else {
		        	return;
		        }

				window.getActivePage().showView(viewId, fileSelected, IWorkbenchPage.VIEW_ACTIVATE);
			} catch (PartInitException e) {
				MessageDialog.openError(window.getShell(), "Error",
						"Error opening view:" + e.getMessage());
			}
		}
	}
}
