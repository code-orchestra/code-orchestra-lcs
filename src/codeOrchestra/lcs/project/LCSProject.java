package codeOrchestra.lcs.project;

import java.io.File;
import java.io.IOException;

import codeOrchestra.actionScript.compiler.fcsh.FSCHCompilerKind;
import codeOrchestra.http.CodeOrchestraResourcesHttpServer;
import codeOrchestra.utils.StringUtils;

/**
 * @author Alexander Eliseyev
 */
public class LCSProject {

  private static LCSProject currentProject;

  public static LCSProject getCurrentProject() {
    return currentProject;
  }
  
  public static void close() {    
    currentProject = null;
  }

  public static LCSProject createNew(String name, String path) {
    currentProject = new LCSProject(path);
    currentProject.name = name;
    currentProject.initDefaultValues();
    currentProject.save();

    return currentProject;
  }

  public static LCSProject loadFrom(String file) {
    currentProject = new LCSProject(file);
    return currentProject;
  }

  private String name;
  private String path;

  private ProjectPreferenceStore preferenceStore;

  private CompilerSettings compilerSettings;
  private SourceSettings sourceSettings;
  private LiveCodingSettings liveCodingSettings;
  private boolean disposed;

  private LCSProject(String descriptorFile) {
    this.path = descriptorFile;
  
    RecentProjects.addRecentProject(descriptorFile);

    preferenceStore = new ProjectPreferenceStore(descriptorFile);
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
    updateExternalPaths();
  }

  public void updateExternalPaths() {
    CodeOrchestraResourcesHttpServer.getInstance().addAlias(getOutputDir(), "/output");
  }

  private void initPaths() {
    File outputDir = getOutputDir();
    if (!outputDir.exists()) {
      outputDir.mkdirs();
    }

    File outputIncrementalDir = new File(outputDir, "livecoding");
    if (!outputIncrementalDir.exists()) {
      outputIncrementalDir.mkdirs();
    }
    
    File digestsDir = getDigestsDir();
    if (!digestsDir.exists()) {
      digestsDir.mkdirs();
    }
  }

  public File getOrCreateIncrementalSourcesDir() {
    File incrementalSourcesDir = new File(getBaseDir(), "incremental");
    if (!incrementalSourcesDir.exists()) {
      incrementalSourcesDir.mkdir();
    }
    return incrementalSourcesDir;
  }

  public File getLinkReportFile() {
    return new File(getOutputDir(), "link-report.xml");
  }
  
  public File getOutputDir() {
    String outputPath = compilerSettings.getOutputPath();
    if (StringUtils.isEmpty(outputPath)) {
      return getDefaultOutputDir();
    }
    return new File(outputPath);
  }
  
  public File getDefaultOutputDir() {
    return new File(getBaseDir(), "colt_output");
  }
  
  public File getDigestsDir() {
    return new File(getBaseDir(), "digests");
  }

  public File getBaseDir() {
    return new File(path).getParentFile();
  }

  public String getFlexConfigPath(LCSProject currentProject, FSCHCompilerKind compilerKind) {
    return new File(currentProject.getBaseDir(), currentProject.getName() + "_" + compilerKind.getCommandName() + "_flex_config.xml").getPath();
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

  public ProjectPreferenceStore getPreferenceStore() {
    return preferenceStore;
  }

  private void initDefaultValues() {
    preferenceStore.setValue("outputPath", getDefaultOutputDir().getPath());
    preferenceStore.setValue("useDefaultSDKConfiguration", true);
    preferenceStore.setValue("clearMessages", true);
    preferenceStore.setValue("disconnectOnTimeout", true);
    preferenceStore.setValue("excludeUnusedCode", true);
    preferenceStore.setValue("compilationTimeoutValue", "30");
  }
  
  public void save() {
    try {
      preferenceStore.setValue("name", name);
      preferenceStore.save();
    } catch (IOException e) {
      throw new RuntimeException("Can't save the project " + name, e);
    }
  }
  
  public File getOutputFile() {
    return new File(getOutputDir(), compilerSettings.getOutputFilename());
  }

  public boolean isDisposed() {
    return disposed;
  }
  
  public void setDisposed() {
    this.disposed = true;
  }

}
