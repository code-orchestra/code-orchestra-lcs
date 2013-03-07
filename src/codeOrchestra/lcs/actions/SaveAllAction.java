package codeOrchestra.lcs.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;

import codeOrchestra.lcs.ICommandIds;
import codeOrchestra.lcs.LiveConfigurationView;

public class SaveAllAction extends Action {
	
	private final IWorkbenchWindow window;
	private final String viewId;

	public SaveAllAction(IWorkbenchWindow window, String label, String viewId) {
		this.window = window;
		this.viewId = viewId;
		setText(label);
		setId(ICommandIds.CMD_SAVE_ALL);
		setActionDefinitionId(ICommandIds.CMD_SAVE_ALL);
		setImageDescriptor(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/save.gif"));
	}
	
	@Override
	public boolean isEnabled() {
		if (window == null) {
			return false;
		}
		
		if (window.getActivePage() == null) {
			return false;
		}
		
		for (IViewPart viewPart : window.getActivePage().getViews()) {
			if (viewPart instanceof LiveConfigurationView) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void run() {
		if (window != null) {
			for (IViewPart viewPart : window.getActivePage().getViews()) {
				if (viewPart instanceof LiveConfigurationView) {
					((LiveConfigurationView) viewPart).save();
				}
			}
		}
	}
	

}
