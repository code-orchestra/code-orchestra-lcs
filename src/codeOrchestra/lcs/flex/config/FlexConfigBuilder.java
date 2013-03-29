package codeOrchestra.lcs.flex.config;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

import codeOrchestra.lcs.LCSException;
import codeOrchestra.lcs.project.CompilerSettings;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.project.SourceSettings;
import codeOrchestra.lcs.run.LiveCodingAnnotation;
import codeOrchestra.lcs.sources.SourceFile;
import codeOrchestra.utils.FileUtils;
import codeOrchestra.utils.NameUtil;
import codeOrchestra.utils.StringUtils;

/**
 * @author Alexander Eliseyev
 */
public class FlexConfigBuilder {

  private final LCSProject project;
  private final boolean incrementalCompilation;
  private final List<SourceFile> changedFiles;

  public FlexConfigBuilder(LCSProject project, boolean incrementalCompilation, List<SourceFile> changedFiles) {
    this.project = project;
    this.incrementalCompilation = incrementalCompilation;
    this.changedFiles = changedFiles;
  }

  public FlexConfig build() throws LCSException {
    CompilerSettings compilerSettings = project.getCompilerSettings();
    FlexConfig flexConfig = new FlexConfig(incrementalCompilation, false);

    // Sources
    if (!incrementalCompilation) {
      for (String sourcePath : project.getSourceSettings().getSourcePaths()) {
        flexConfig.addSourcePath(sourcePath);
      }
    }

    // Incremental settings
    if (incrementalCompilation) {
      // Add root module generated sources
      String outputFileName = compilerSettings.getOutputFilename().replaceFirst("\\.swf$", ".swc");
      flexConfig.addLibraryPath(compilerSettings.getOutputPath() + "/" + outputFileName);

      // Load root module link report file for externs for Live-Coding
      // incremental module
      flexConfig.setLoadExternsFilePath(getLinkReportFilePath());

      // CO-4487 - compiler shenanigans for incremental mode
      flexConfig.setStrict(false);
      flexConfig.setVerifyDigests(false);
      flexConfig.setWarnings(false);
      flexConfig.setIncremental(true);
      
      // Changed source files must be copied to a separate folder and added to the config
      File incrementalSourcesDir = project.getOrCreateIncrementalSourcesDir();
      FileUtils.clear(incrementalSourcesDir);
      for (SourceFile sourceFile : changedFiles) {
        try {
          FileUtils.copyFileChecked(sourceFile.getFile(), new File(incrementalSourcesDir, sourceFile.getRelativePath()), false);
        } catch (IOException e) {
          throw new LCSException("Can't copy changed source file to 'incremental' dir", e);
        }
      }
      flexConfig.addSourcePath(incrementalSourcesDir.getPath());
    }

    // Link report file generation
    flexConfig.setLinkReportFilePath(getLinkReportFilePath());

    // Output path
    flexConfig.setOutputPath(compilerSettings.getOutputPath() + File.separator + compilerSettings.getOutputFilename());

    // Libraries
    for (String libraryPath : project.getSourceSettings().getLibraryPaths()) {
      flexConfig.addLibraryPath(libraryPath);
    }

    // Main class
    if (!incrementalCompilation) {
      flexConfig.addFileSpecPathElement(compilerSettings.getMainClass());
    }

    // Target player version
    flexConfig.setTargetPlayerVersion(compilerSettings.getTargetPlayerVersion());

    // Include classes (SWC)
    if (incrementalCompilation) {
      for (SourceFile changedSourceFile : changedFiles) {
        flexConfig.addClass(changedSourceFile.getFqName());
      }
    }

    // RSL
    flexConfig.setRuntimeSharedLibrary(compilerSettings.useFrameworkAsRSL());

    // Locale
    if (compilerSettings.useNonDefaultLocale()) {
      flexConfig.setLocale(compilerSettings.getLocaleOptions());
    }

    // Custom metadata
    for (LiveCodingAnnotation lca : LiveCodingAnnotation.values()) {
      flexConfig.addCustomMetadata(lca.name());
    }

    return flexConfig;
  }

  public static void addLibraryClasses(FlexConfig flexConfig, SourceSettings sourceSettings) {
    for (String sourcePath : sourceSettings.getSourcePaths()) {
      File sourceDir = new File(sourcePath);
      if (!sourceDir.exists() || !sourceDir.isDirectory()) {
        continue;
      }

      List<File> sourceFiles = FileUtils.listFileRecursively(sourceDir, new FileFilter() {
        @Override
        public boolean accept(File file) {
          String filenameLowerCase = file.getName().toLowerCase();
          return filenameLowerCase.endsWith(".as") || filenameLowerCase.endsWith(".mxml");
        }
      });

      for (File sourceFile : sourceFiles) {
        String relativePath = FileUtils.getRelativePath(sourceFile.getPath(), sourceDir.getPath(), File.separator);

        if (!StringUtils.isEmpty(relativePath)) {
          String fqName = NameUtil.namespaceFromPath(relativePath);
          flexConfig.addClass(fqName);
        }
      }
    }
  }

  private String getLinkReportFilePath() {
    return new File(project.getCompilerSettings().getOutputPath(), "link-report.xml").getPath();
  }

}
