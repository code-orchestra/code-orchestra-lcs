package codeOrchestra.lcs.air.views.android;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchWindow;

import codeOrchestra.lcs.air.AirBuildScriptGenerator;
import codeOrchestra.lcs.air.android.AirAndroidApkBuildScriptGenerator;
import codeOrchestra.lcs.air.views.AirLaunchCustomizationDialogShell;
import codeOrchestra.lcs.air.views.AirOptions;
import codeOrchestra.lcs.air.views.utils.ViewHelper;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.views.LiveCodingSettingsView;

/**
 * @author Alexander Eliseyev
 */
public class AirAndroidLaunchCustomizationDialogShell extends AirLaunchCustomizationDialogShell {
  
  public AirAndroidLaunchCustomizationDialogShell(IWorkbenchWindow window, IPreferenceStore preferenceStore) {
    super(window, preferenceStore, "Android: customize launch");
  }

  @Override
  protected AirOptions createOptions(Composite parent) {
    return new AirAndroidOptions(parent, getPreferenceStore(), 3);
  }

  @Override
  protected AirBuildScriptGenerator createBuildScriptGenerator(LCSProject project, IWorkbenchWindow window) {
    return new AirAndroidApkBuildScriptGenerator(project, window);
  }

  @Override
  protected void updateScriptEditorValue(IWorkbenchWindow window, String scriptPath) {
    LiveCodingSettingsView lcsv = (LiveCodingSettingsView) ViewHelper.getView(window, LiveCodingSettingsView.ID);
    if (lcsv != null) {
      lcsv.setAirAndroidScriptEditorValue(scriptPath);
    }   
  }

}
