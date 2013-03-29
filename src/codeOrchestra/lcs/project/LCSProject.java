package codeOrchestra.lcs.project;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.preference.PreferenceStore;

/**
 * @author Alexander Eliseyev
 */
public class LCSProject {

	private static LCSProject currentProject;
	
	public static LCSProject getCurrentProject() {
		return currentProject;
	}
	
	public static LCSProject createNew(String name, String path) {
		currentProject = new LCSProject(path);
		currentProject.name = name;
		currentProject.save();
		
		return currentProject;
	}
	
	public static LCSProject loadFrom(String file) {
		currentProject = new LCSProject(file);		
		return currentProject;
	}
	
	private String name;
	private String path;
	
	private PreferenceStore preferenceStore; 
	
	private CompilerSettings compilerSettings;
	private SourceSettings sourceSettings;
	private LiveCodingSettings liveCodingSettings;	
	
	private LCSProject(String descriptorFile) {
		this.path = descriptorFile;
	  
	  preferenceStore = new PreferenceStore(descriptorFile);
		try {
			preferenceStore.load();
		} catch (IOException e) {
			throw new RuntimeException("Error loading live coding configuration", e);
		}
		
		this.name = preferenceStore.getString("name");
		
		compilerSettings = new CompilerSettings(preferenceStore);
		sourceSettings = new SourceSettings(preferenceStore);
		liveCodingSettings = new LiveCodingSettings(preferenceStore);
		
		initPaths();
	}
	
	private void initPaths() {    
    File outputDir = getOutputDir();
    if (!outputDir.exists()) {
      outputDir.mkdir();
    }
  }

	public File getOrCreateIncrementalSourcesDir() {
	  File incrementalSourcesDir = new File(getBaseDir(), "incremental");
	  if (!incrementalSourcesDir.exists()) {
	    incrementalSourcesDir.mkdir();
	  }
	  return incrementalSourcesDir;
	}
	
  public File getOutputDir() {
    return new File(getBaseDir(), "lcs_output");
  }

  public File getBaseDir() {
    return new File(path).getParentFile();
  }

  public String getFlexConfigPath(LCSProject currentProject) {
    return new File(currentProject.getBaseDir(), currentProject.getName() + "_flex_config.xml").getPath();
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

	public String getPath() {
    return path;
  }
	
  public PreferenceStore getPreferenceStore() {
    return preferenceStore;
  }

  public void save() {
		try {
		  preferenceStore.setValue("name", name);
		  preferenceStore.save();
		} catch (IOException e) {
			throw new RuntimeException("Can't save the project " + name, e);
		}
	}
	
}
