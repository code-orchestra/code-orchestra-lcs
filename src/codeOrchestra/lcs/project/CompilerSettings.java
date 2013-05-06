package codeOrchestra.lcs.project;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Alexander Eliseyev
 */
public class CompilerSettings extends ProjectSettingsPart {

	public CompilerSettings(IPreferenceStore preferenceStore) {
		super(preferenceStore);
	}	
	
	public String getFlexSDKPath() {
	  return getPreferenceStore().getString("flexSDKPath");
	}
	
	public boolean useStrictMode() {
	  return getPreferenceStore().getBoolean("compilerStrict");	  
	}
	
	public boolean useDefaultSDKConfiguration() {
	  return getPreferenceStore().getBoolean("useDefaultSDKConfiguration");
	}
	
	public boolean useCustomSDKConfiguration() {
	  return getPreferenceStore().getBoolean("useCustomSDKConfiguration");
	}
	
	public String getCustomConfigPath() {
	  return getPreferenceStore().getString("customConfigPath");
	}
	
	public String getMainClass() {
	  return getPreferenceStore().getString("mainClass");
	}
	
	public String getAirScript() {
		  return getPreferenceStore().getString("airScript");
		}
	
	public String getOutputFilename() {
	  return getPreferenceStore().getString("outputFileName");
	}
	
	/*
	public String getOutputPath() {
	  return getPreferenceStore().getString("outputPath");
	}
	*/
	
	public String getTargetPlayerVersion() {
	  return getPreferenceStore().getString("targetPlayerVersion");
	}
	
	public boolean useFrameworkAsRSL() {
	  return getPreferenceStore().getBoolean("useFrameworkAsRSL");
	}
	
	public boolean useNonDefaultLocale() {
	  return getPreferenceStore().getBoolean("nonDefaultLocale");
	}
	
	public String getLocaleOptions() {
	  return getPreferenceStore().getString("localeOptions");
	}
	
	public String getCompilerOptions() {
	  return getPreferenceStore().getString("compilerOptions");
	}
	
}
