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
	
	public List<String> getSourcePaths() {
	  return getPaths("sourcePaths");
	}

	public List<String> getLibraryPaths() {
	  return getPaths("libraryPaths");
	}
	
}
