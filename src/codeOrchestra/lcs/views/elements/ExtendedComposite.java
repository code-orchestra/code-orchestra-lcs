package codeOrchestra.lcs.views.elements;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public class ExtendedComposite extends Composite {
	public ExtendedComposite(Composite parent, int style) {
		super(parent, style);
	}

	private void setVisibleInSlotImpl(boolean isVisible) {
    	setVisible(isVisible);
    	((GridData)getLayoutData()).exclude = !isVisible;
	}
	
	public void setVisibleInSlot(boolean isVisible, int parentDepth, Composite anchorComponent) {
		setVisibleInSlotImpl(isVisible);
		
    	int i = 0;
    	if (null==anchorComponent) {
    		anchorComponent = this;
    	}
    	Composite parent = getParent();
    	while (i<parentDepth) {
    		parent.layout();
    		parent = parent.getParent();
    	}
	}
	
	public void setVisibleInSlot(boolean isVisible, Composite anchorComponent) {
		setVisibleInSlotImpl(isVisible);
		
    	Composite parent = getParent();
    	while (null!=parent) {
    		parent.layout();
    		parent = parent.getParent();
    	}
	}
	
	public void setVisibleInSlot(boolean isVisible) {
		setVisibleInSlot(isVisible,null);	
	}
	
	public void hide() {
		setVisibleInSlot(false);
	}

	public void show() {
		setVisibleInSlot(true);
	}
}
