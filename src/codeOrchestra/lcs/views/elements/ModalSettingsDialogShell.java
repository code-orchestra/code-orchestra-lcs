package codeOrchestra.lcs.views.elements;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ModalSettingsDialogShell implements IRelocableComponent {
	private Shell baseDialogShell; 

	public ModalSettingsDialogShell() {
		Display display = Display.getDefault();
		final Shell shell = new Shell(display, SWT.SHELL_TRIM);
		baseDialogShell = new Shell(shell, SWT.PRIMARY_MODAL | SWT.SHEET);
		center();
	}
	
	public ModalSettingsDialogShell(String title, int width, int height) {
		this();
		final Shell dlg = getBaseDialogShell();
		dlg.setText(title);
		dlg.setSize(new Point(width,height));
		center();
	}
	
	public ModalSettingsDialogShell(String title) {
		this();
		final Shell dlg = getBaseDialogShell();
		dlg.setText(title);
		dlg.pack(true);
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
		getBaseDialogShell().setVisible(isVisible);
	}

	public Shell getBaseDialogShell() {
		return baseDialogShell;
	}

	public void setBaseDialogShell(Shell baseDialogShell) {
		this.baseDialogShell = baseDialogShell;
	}

	@Override
	public void setLocation(int x, int y) {
		getBaseDialogShell().setLocation(new Point(x,y));
	}

	@Override
	public int getLocationX() {
		return getBaseDialogShell().getLocation().x;
	}

	@Override
	public int getLocationY() {
		return getBaseDialogShell().getLocation().y;
	}

	@Override
	public int getWidth() {
		return getBaseDialogShell().getSize().x;
	}

	@Override
	public int getHeight() {		
		return getBaseDialogShell().getSize().y;
	}

	
}