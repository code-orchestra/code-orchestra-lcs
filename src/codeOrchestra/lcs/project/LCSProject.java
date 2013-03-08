package codeOrchestra.lcs.project;

import java.io.IOException;

import org.eclipse.jface.preference.PreferenceStore;

import codeOrchestra.lcs.config.view.LiveConfigViewStack;

public class LCSProject {

	private static LCSProject currentProject;
	
	public static LCSProject getCurrentProject() {
		return currentProject;
	}
	
	public static LCSProject createNew(String name, String file) {
		currentProject = new LCSProject(file);
		currentProject.name = name;
		currentProject.save();
		
		return currentProject;
	}
	
	public static LCSProject loadFrom(String file) {
		currentProject = new LCSProject(file);		
		return currentProject;
	}
	
	private String name;
	
	private PreferenceStore preferenceStore; 
	
	private CompilerSettings compilerSettings;
	private SourceSettings sourceSettings;
	private LiveCodingSettings liveCodingSettings;	
	
	private LCSProject(String descriptorFile) {
		preferenceStore = new PreferenceStore(LiveConfigViewStack.lastPath);
		try {
			preferenceStore.load();
		} catch (IOException e) {
			throw new RuntimeException("Error loading live coding configuration", e);
		}
		
		compilerSettings = new CompilerSettings(preferenceStore);
		sourceSettings = new SourceSettings(preferenceStore);
		liveCodingSettings = new LiveCodingSettings(preferenceStore);
	}

	public CompilerSettings getCompilerSettings() {
		return compilerSettings;
	}

	public SourceSettings getSourceSettings() {
		return sourceSettings;
	}

	public LiveCodingSettings getLiveCodingSettings() {
		return liveCodingSettings;
	}
	
	public String getName() {
		return name;
	}

	public void save() {
		try {
			preferenceStore.save();
		} catch (IOException e) {
			throw new RuntimeException("Can't save the project " + name, e);
		}
	}
	
}
