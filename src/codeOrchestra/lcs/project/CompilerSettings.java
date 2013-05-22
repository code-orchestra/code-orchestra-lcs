package codeOrchestra.lcs.project;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import codeOrchestra.utils.StringUtils;

/**
 * @author Alexander Eliseyev
 */
public class CompilerSettings extends ProjectSettingsPart {

  public CompilerSettings(IPreferenceStore preferenceStore) {
    super(preferenceStore);
  }

  public boolean excludeUnusedCode() {
    return getPreferenceStore().getBoolean("excludeUnusedCode");
  }

  public List<String> getExcludedClasses() {
    List<String> excludedClasses = new ArrayList<String>();

    String excludedClassesStr = getPreferenceStore().getString("excludedClasses");
    if (excludedClassesStr != null) {
      String[] split = excludedClassesStr.split("\\|");
      for (int i = 0; i < split.length; i++) {
        String fqName = split[i].trim();
        if (StringUtils.isNotEmpty(fqName) && !excludedClasses.contains(fqName)) {
          excludedClasses.add(fqName);
        }
      }
    }

    return excludedClasses;
  }

  public boolean interruptCompilationByTimeout() {
    return getPreferenceStore().getBoolean("compilationTimeout");
  }

  public int getCompilationTimeout() {
    return getPreferenceStore().getInt("compilationTimeoutValue");
  }

  public String getFlexSDKPath() {
    return getPreferenceStore().getString("flexSDKPath");
  }

  public void setFlexSDKPath(String flexSDKPath) {
    getPreferenceStore().setValue("flexSDKPath", flexSDKPath);
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

  public void setCustomConfigPath(String customConfigPath) {
    getPreferenceStore().setValue("customConfigPath", customConfigPath);
  }

  public String getMainClass() {
    return getPreferenceStore().getString("mainClass");
  }

  public void setMainClass(String mainClass) {
    getPreferenceStore().setValue("mainClass", mainClass);
  }

  public String getAirScript() {
    return getPreferenceStore().getString("airScript");
  }

  public String getOutputFilename() {
    return getPreferenceStore().getString("outputFileName");
  }

  public void setOutputFilename(String outputFileName) {
    getPreferenceStore().setValue("outputFileName", outputFileName);
  }

  public String getOutputPath() {
    return getPreferenceStore().getString("outputPath");
  }

  public void setOutputPath(String outputPath) {
    getPreferenceStore().setValue("outputPath", outputPath);
  }

  public String getTargetPlayerVersion() {
    return getPreferenceStore().getString("targetPlayerVersion");
  }
  
  public void setTargetPlayerVersion(String targetPlayerVersion) {
    getPreferenceStore().setValue("targetPlayerVersion", targetPlayerVersion);
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

  public void setCompilerOptions(String compilerOptions) {
    getPreferenceStore().setValue("compilerOptions", compilerOptions);    
  }

}
