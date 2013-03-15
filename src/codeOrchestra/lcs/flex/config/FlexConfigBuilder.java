package codeOrchestra.lcs.flex.config;

import java.io.File;

import codeOrchestra.lcs.LCSException;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.run.LiveCodingAnnotation;

/**
 * @author Alexander Eliseyev
 */
public class FlexConfigBuilder {
  
  private LCSProject project;
  private final boolean incrementalCompilation;

  public FlexConfigBuilder(LCSProject project, boolean incrementalCompilation) {
    this.project = project;
    this.incrementalCompilation = incrementalCompilation;
  }
  
  public FlexConfig build() throws LCSException {
    FlexConfig flexConfig = new FlexConfig(!incrementalCompilation, false);
    
    // Sources
    for (String sourcePath : project.getSourceSettings().getSourcePaths()) {
      flexConfig.addSourcePath(sourcePath);      
    }
    
    // Incremental settings
    if (incrementalCompilation) {
      // Add root module generated sources
      String outputFileName = project.getCompilerSettings().getOutputFilename().replaceFirst("\\.swf$", ".swc");
      flexConfig.addLibraryPath(project.getCompilerSettings().getOutputPath() + "/" + outputFileName);
      
      // Load root module link report file for externs for Live-Coding incremental module
      flexConfig.setLoadExternsFilePath(getLinkReportFilePath());
      
      // CO-4487 - compiler shenanigans for incremental mode
      flexConfig.setStrict(false);
      flexConfig.setVerifyDigests(false);
      flexConfig.setWarnings(false);
      flexConfig.setIncremental(true);
    }
    
    // Link report file generation
    flexConfig.setLinkReportFilePath(getLinkReportFilePath());
    
    // Output path
    flexConfig.setOutputPath(project.getCompilerSettings().getOutputPath());
    
    // Libraries
    for (String libraryPath : project.getSourceSettings().getLibraryPaths()) {
      flexConfig.addLibraryPath(libraryPath);
    }
    
    // Main class
    if (!incrementalCompilation) {
      flexConfig.addFileSpecPathElement(project.getCompilerSettings().getMainClass());
    }
    
    // Target player version
    flexConfig.setTargetPlayerVersion(project.getCompilerSettings().getTargetPlayerVersion());
    

    // Include classes (SWC)
    if (incrementalCompilation) {
      // TODO: figure this out!
    }
    
    // RSL
    flexConfig.setRuntimeSharedLibrary(project.getCompilerSettings().useFrameworkAsRSL());

    // Locale
    if (project.getCompilerSettings().useNonDefaultLocale()) {
      flexConfig.setLocale(project.getCompilerSettings().getLocaleOptions());
    }
    
    // Custom metadata
    for (LiveCodingAnnotation lca : LiveCodingAnnotation.values()) {
      flexConfig.addCustomMetadata(lca.name());      
    }
    
    return flexConfig;
  }

  private String getLinkReportFilePath() {
    return new File(project.getCompilerSettings().getOutputPath(), "link-report.xml").getPath();
  }

}
