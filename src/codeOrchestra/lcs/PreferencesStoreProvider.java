package codeOrchestra.lcs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PreferencesStoreProvider {

	private static Map<String, IPreferenceStore> storeMap = new HashMap<String, IPreferenceStore>(); 
	
	public static IPreferenceStore getPreferencesStore(String name) {
		IPreferenceStore preferenceStore = storeMap.get(name);
		if (preferenceStore == null) {
			preferenceStore = new ScopedPreferenceStore(new InstanceScope(), name); 
		}
		return preferenceStore;
	}
	
}
