package codeOrchestra.lcs.project;

import org.eclipse.jface.preference.IPreferenceStore;

import codeOrchestra.lcs.run.LauncherType;
import codeOrchestra.lcs.run.LiveMethods;
import codeOrchestra.lcs.run.Target;

/**
 * @author Alexander Eliseyev
 */
public class LiveCodingSettings extends ProjectSettingsPart {

  private static final int DEFAULT_MAX_LOOPS_COUNT = 10000;

  public LiveCodingSettings(IPreferenceStore preferenceStore) {
    super(preferenceStore);
  }

  public LauncherType getLauncherType() {
    return getPreferenceStore().getBoolean("flashPlayerLauncher") ? LauncherType.FLASH_PLAYER : LauncherType.DEFAULT;
  }

  public String getFlashPlayerPath() {
    return getPreferenceStore().getString("flashPlayerPath");
  }

  public void setFlashPlayerPath(String flashPlayerPath) {
    getPreferenceStore().setValue("flashPlayerPath", flashPlayerPath);
  }

  public String getWebAddress() {
    return getPreferenceStore().getString("webAddress");
  }

  public LiveMethods getLiveMethods() {
    String liveMethodsStringValue = getPreferenceStore().getString("liveMethods");
    if (liveMethodsStringValue == null) {
      return LiveMethods.ANNOTATED;
    }

    return LiveMethods.parseValue(liveMethodsStringValue);
  }

  public Target getLaunchTarget() {
    return Target.parse(getPreferenceStore().getString("target"));
  }

  public boolean clearMessagesOnSessionStart() {
    return getPreferenceStore().getBoolean("clearMessages");
  }

  public boolean disconnectOnTimeout() {
    return getPreferenceStore().getBoolean("disconnectOnTimeout");
  }

  public boolean startSessionPaused() {
    return getPreferenceStore().getBoolean("startSessionPaused");
  }

  public boolean makeGettersSettersLive() {
    return getPreferenceStore().getBoolean("makeGettersSettersLive");
  }

  public int getMaxIterationsCount() {
    String maxLoopsStr = getPreferenceStore().getString("maxLoopIterations");
    if (maxLoopsStr == null) {
      return DEFAULT_MAX_LOOPS_COUNT;
    }

    return Integer.valueOf(maxLoopsStr);
  }

}
