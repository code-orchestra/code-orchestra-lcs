package codeOrchestra.lcs.project;

import org.eclipse.jface.preference.IPreferenceStore;

public abstract class ProjectSettingsPart {

	protected IPreferenceStore preferenceStore;

	public ProjectSettingsPart(IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
	}		
	
}
