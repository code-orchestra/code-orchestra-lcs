package codeOrchestra.lcs.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import codeOrchestra.lcs.ICommandIds;
import codeOrchestra.lcs.config.view.LiveConfigViewStack;

public class OpenLiveCodingConfigurationAction extends Action {

	private final IWorkbenchWindow window;
	private final String viewId;

	public OpenLiveCodingConfigurationAction(IWorkbenchWindow window,
			String label, String viewId) {
		this.window = window;
		this.viewId = viewId;
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
				dialog.setFilterExtensions(new String[] {"*.lcc"});
		        dialog.setFilterNames(new String[] {"Live Coding Configuration"});
				String fileSelected = dialog.open();

				if (fileSelected != null) {
					LiveConfigViewStack.lastPath = fileSelected;
					window.getActivePage().showView(viewId, fileSelected, IWorkbenchPage.VIEW_ACTIVATE);
				}
			} catch (PartInitException e) {
				MessageDialog.openError(window.getShell(), "Error",
						"Error opening view:" + e.getMessage());
			}
		}
	}

}
