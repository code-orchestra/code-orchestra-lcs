package codeOrchestra.lcs.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import codeOrchestra.lcs.ICommandIds;
import codeOrchestra.lcs.LiveConfigurationView;

/**
 * @author Alexander Eliseyev
 */
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
		
		setEnabled(false);
		
		window.addPerspectiveListener(new IPerspectiveListener() {			
			@Override
			public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
				setEnabled(shouldBeEnabled());
			}
			
			@Override
			public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
			}
		});
	}
	
	public boolean shouldBeEnabled() {
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
