package codeOrchestra.lcs.air.views.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class ModalSettingsDialogShell implements IRelocableComponent {
	
	private Shell baseDialogShell; 

	private ModalSettingsDialogShell() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		this.baseDialogShell = new Shell(shell, SWT.PRIMARY_MODAL | SWT.SHELL_TRIM);
	}
	
	public ModalSettingsDialogShell(String title) {
		this();
		this.baseDialogShell.setText(title);
		this.baseDialogShell.pack(true);
		center();
	}
	
	public ModalSettingsDialogShell(String title, int width, int height) {
		this();
		this.baseDialogShell.setText(title);
		this.baseDialogShell.setSize(new Point(width,height));
		center();
	}
	
	public void center() {
		ScreenHelper.centerOnScreen(this, true);
	}
	
	public void show() {
		setVisible(true);
	}
	
	public void hide() {
		setVisible(false);
	}	
	
	public void setVisible(boolean isVisible) {
		this.baseDialogShell.setVisible(isVisible);
	}

	public Shell getBaseDialogShell() {
		return this.baseDialogShell;
	}

	public void setBaseDialogShell(Shell baseDialogShell) {
		this.baseDialogShell = baseDialogShell;
	}

	@Override
	public void setLocation(int x, int y) {
		this.baseDialogShell.setLocation(new Point(x,y));
	}

	@Override
	public int getLocationX() {
		return this.baseDialogShell.getLocation().x;
	}

	@Override
	public int getLocationY() {
		return this.baseDialogShell.getLocation().y;
	}

	@Override
	public int getWidth() {
		return this.baseDialogShell.getSize().x;
	}

	@Override
	public int getHeight() {		
		return this.baseDialogShell.getSize().y;
	}

	
}