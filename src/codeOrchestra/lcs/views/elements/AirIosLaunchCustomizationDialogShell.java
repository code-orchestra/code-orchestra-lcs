package codeOrchestra.lcs.views.elements;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class AirIosLaunchCustomizationDialogShell extends ModalSettingsDialogShell {
	private Shell baseDialogShell;
	private AirIosOptions airIosOptions;

	public AirIosLaunchCustomizationDialogShell(IPreferenceStore preferenceStore) {
		super("Apple iOS: customize launch",800,600);
		airIosOptions = new AirIosOptions(this.getBaseDialogShell(), preferenceStore, 2);
		airIosOptions.loadOptions();
	}
}
