package codeOrchestra.lcs.project;

import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Alexander Eliseyev
 */
public class SourceSettings extends ProjectSettingsPart {

  public SourceSettings(IPreferenceStore preferenceStore) {
    super(preferenceStore);
  }

  public String getHTMLTemplatePath() {
    return getPreferenceStore().getString("htmlTemplatePath");
  }

  public void setHTMLTemplatePath(String htmlTemplatePath) {
    getPreferenceStore().setValue("htmlTemplatePath", htmlTemplatePath);
  }

  public List<String> getAssetPaths() {
    return getPaths("asssetPaths");
  }

  public void addAssetPath(String asssetPath) {
    addPath("asssetPaths", asssetPath);
  }

  public List<String> getSourcePaths() {
    return getPaths("sourcePaths");
  }

  public void addSourcePath(String sourcePath) {
    addPath("sourcePaths", sourcePath);
  }

  public List<String> getLibraryPaths() {
    return getPaths("libraryPaths");
  }

  public void addLibraryPath(String libraryPath) {
    addPath("libraryPaths", libraryPath);
  }

}
