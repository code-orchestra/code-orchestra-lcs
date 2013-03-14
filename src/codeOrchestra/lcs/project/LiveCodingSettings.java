package codeOrchestra.lcs.project;

import org.eclipse.jface.preference.IPreferenceStore;

import codeOrchestra.lcs.run.LauncherType;
import codeOrchestra.lcs.run.LiveMethods;

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
	
	public LiveMethods getLiveMethods() {
	  String liveMethodsStringValue = getPreferenceStore().getString("liveMethods");
	  if (liveMethodsStringValue == null) {
	    return LiveMethods.ANNOTATED;
	  }
	  
	  return LiveMethods.parseValue(liveMethodsStringValue);
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
