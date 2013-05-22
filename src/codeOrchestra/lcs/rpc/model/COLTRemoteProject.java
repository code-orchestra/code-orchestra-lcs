package codeOrchestra.lcs.rpc.model;

import codeOrchestra.lcs.project.LCSProject;

/**
 * @author Alexander Eliseyev
 */
public class COLTRemoteProject {

  private String parentIDEProjectPath;

  private String path;
  private String name;

  private String[] sources;
  private String[] libraries;
  private String[] assets;

  private String htmlTemplateDir;

  private String flashPlayerPath;
  private String flexSDKPath;
  private String customConfigPath;

  private String mainClass;

  private String outputFileName;
  private String outputPath;
  private String targetPlayerVersion;
  private String compilerOptions;

  public void copyTo(LCSProject coltProject) {
    if (sources != null) {
      for (String sourcePath : sources) {
        coltProject.getSourceSettings().addSourcePath(sourcePath);
      }
    }
    if (libraries != null) {
      for (String libraryPath : libraries) {
        coltProject.getSourceSettings().addLibraryPath(libraryPath);
      }
    }
    if (assets != null) {
      for (String assetPath : assets) {
        coltProject.getSourceSettings().addAssetPath(assetPath);
      }
    }
    if (htmlTemplateDir != null) {
      coltProject.getSourceSettings().setHTMLTemplatePath(htmlTemplateDir);
    }
    if (flashPlayerPath != null) {
      coltProject.getLiveCodingSettings().setFlashPlayerPath(flashPlayerPath);
    }
    if (flexSDKPath != null) {
      coltProject.getCompilerSettings().setFlexSDKPath(flexSDKPath);
    }
    if (customConfigPath != null) {
      coltProject.getCompilerSettings().setCustomConfigPath(customConfigPath);
    }
    if (mainClass != null) {
      coltProject.getCompilerSettings().setMainClass(mainClass);
    }
    if (outputFileName != null) {
      coltProject.getCompilerSettings().setOutputFilename(outputFileName);
    }
    if (outputPath != null) {
      coltProject.getCompilerSettings().setOutputPath(outputPath);
    }
    if (targetPlayerVersion != null) {
      coltProject.getCompilerSettings().setTargetPlayerVersion(targetPlayerVersion);
    }
    if (compilerOptions != null) {
      coltProject.getCompilerSettings().setCompilerOptions(compilerOptions);
    }
  }

  public String getParentIDEProjectPath() {
    return parentIDEProjectPath;
  }

  public void setParentIDEProjectPath(String parentIDEProjectPath) {
    this.parentIDEProjectPath = parentIDEProjectPath;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String[] getSources() {
    return sources;
  }

  public void setSources(String[] sources) {
    this.sources = sources;
  }

  public String[] getLibraries() {
    return libraries;
  }

  public void setLibraries(String[] libraries) {
    this.libraries = libraries;
  }

  public String[] getAssets() {
    return assets;
  }

  public void setAssets(String[] assets) {
    this.assets = assets;
  }

  public String getHtmlTemplateDir() {
    return htmlTemplateDir;
  }

  public void setHtmlTemplateDir(String htmlTemplateDir) {
    this.htmlTemplateDir = htmlTemplateDir;
  }

  public String getFlashPlayerPath() {
    return flashPlayerPath;
  }

  public void setFlashPlayerPath(String flashPlayerPath) {
    this.flashPlayerPath = flashPlayerPath;
  }

  public String getFlexSDKPath() {
    return flexSDKPath;
  }

  public void setFlexSDKPath(String flexSDKPath) {
    this.flexSDKPath = flexSDKPath;
  }

  public String getCustomConfigPath() {
    return customConfigPath;
  }

  public void setCustomConfigPath(String customConfigPath) {
    this.customConfigPath = customConfigPath;
  }

  public String getMainClass() {
    return mainClass;
  }

  public void setMainClass(String mainClass) {
    this.mainClass = mainClass;
  }

  public String getOutputFileName() {
    return outputFileName;
  }

  public void setOutputFileName(String outputFileName) {
    this.outputFileName = outputFileName;
  }

  public String getOutputPath() {
    return outputPath;
  }

  public void setOutputPath(String outputPath) {
    this.outputPath = outputPath;
  }

  public String getTargetPlayerVersion() {
    return targetPlayerVersion;
  }

  public void setTargetPlayerVersion(String targetPlayerVersion) {
    this.targetPlayerVersion = targetPlayerVersion;
  }

  public String getCompilerOptions() {
    return compilerOptions;
  }

  public void setCompilerOptions(String compilerOptions) {
    this.compilerOptions = compilerOptions;
  }

}
