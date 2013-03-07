package codeOrchestra.lcs;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import codeOrchestra.lcs.config.view.ConfigurationNameInputValidator;
import codeOrchestra.lcs.config.view.LiveConfigViewStack;

public class OpenViewAction extends Action {

	private final IWorkbenchWindow window;
	private final String viewId;

	public OpenViewAction(IWorkbenchWindow window, String label, String viewId) {
		this.window = window;
		this.viewId = viewId;
		setText(label);
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_OPEN);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_OPEN);
		setImageDescriptor(codeOrchestra.lcs.Activator
				.getImageDescriptor("/icons/sample2.gif"));
	}

	public void run() {
		if (window != null) {
			try {
				String secondaryId = null;

				InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(), "New Live Run Configuration", "Enter a configuration name", "", new ConfigurationNameInputValidator());
				if (dlg.open() == Window.OK) {
					secondaryId = dlg.getValue();
				} else {
					return;
				}

				LiveConfigViewStack.lastName = secondaryId;
				window.getActivePage().showView(viewId, secondaryId,
						IWorkbenchPage.VIEW_ACTIVATE);
			} catch (PartInitException e) {
				MessageDialog.openError(window.getShell(), "Error",
						"Error opening view:" + e.getMessage());
			}
		}
	}
}
