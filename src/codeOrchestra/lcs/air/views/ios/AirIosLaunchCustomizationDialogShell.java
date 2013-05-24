package codeOrchestra.lcs.air.views.ios;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchWindow;

import codeOrchestra.lcs.air.AirBuildScriptGenerator;
import codeOrchestra.lcs.air.ios.AirIosIpaBuildScriptGenerator;
import codeOrchestra.lcs.air.views.AirLaunchCustomizationDialogShell;
import codeOrchestra.lcs.air.views.AirOptions;
import codeOrchestra.lcs.air.views.utils.ViewHelper;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.views.LiveCodingSettingsView;

/**
 * @author Alexander Eliseyev
 */
public class AirIosLaunchCustomizationDialogShell extends AirLaunchCustomizationDialogShell {

	public AirIosLaunchCustomizationDialogShell(IWorkbenchWindow window, IPreferenceStore preferenceStore) {
		super(window, preferenceStore, "Apple iOS: customize launch");
	}

	@Override
	protected AirOptions createOptions(Composite parent) {
		return new AirIosOptions(parent, getPreferenceStore(), 3);
	}

	@Override
	protected AirBuildScriptGenerator createBuildScriptGenerator(LCSProject project, IWorkbenchWindow window) {
		return new AirIosIpaBuildScriptGenerator(project, window);
	}

	@Override
	protected void updateScriptEditorValue(IWorkbenchWindow window, String scriptPath) {
		LiveCodingSettingsView lcsv = (LiveCodingSettingsView) ViewHelper.getView(window, LiveCodingSettingsView.ID);
		if (lcsv != null) {
			lcsv.setAirIosScriptEditorValue(scriptPath);
		}		
	}

}
