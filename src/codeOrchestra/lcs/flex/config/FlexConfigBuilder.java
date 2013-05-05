package codeOrchestra.lcs.flex.config;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

import codeOrchestra.lcs.LCSException;
import codeOrchestra.lcs.project.CompilerSettings;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.run.LiveCodingAnnotation;
import codeOrchestra.lcs.sources.IgnoredSources;
import codeOrchestra.lcs.sources.SourceFile;
import codeOrchestra.utils.FileUtils;
import codeOrchestra.utils.NameUtil;
import codeOrchestra.utils.PathUtils;
import codeOrchestra.utils.StringUtils;

/**
 * @author Alexander Eliseyev
 */
public class FlexConfigBuilder {
  
  private final LCSProject project;
  private final boolean incrementalCompilation;
  private final List<SourceFile> changedFiles;
  private final boolean assetsUpdateMode;

  public FlexConfigBuilder(LCSProject project, boolean incrementalCompilation, List<SourceFile> changedFiles, boolean skipClear) {
    this.project = project;
    this.incrementalCompilation = incrementalCompilation;
    this.changedFiles = changedFiles;
    this.assetsUpdateMode = skipClear;
  }

  public FlexConfig build() throws LCSException {
    CompilerSettings compilerSettings = project.getCompilerSettings();
    FlexConfig flexConfig = new FlexConfig(incrementalCompilation, false);

    // Sources
    if (!incrementalCompilation) {
      // Defined source paths
      for (String sourcePath : project.getSourceSettings().getSourcePaths()) {
        flexConfig.addSourcePath(sourcePath);
      }
      
      // Provided libraries (language extensions) sources
      flexConfig.addLibraryPath(PathUtils.getColtSWCPath());
    }
    
    // Always strict = false 
    flexConfig.setStrict(compilerSettings.useStrictMode());

    // Incremental settings
    if (incrementalCompilation) {
      // Add root module generated sources
      String outputFileName = compilerSettings.getOutputFilename().replaceFirst("\\.swf$", ".swc");
      flexConfig.addLibraryPath(project.getOutputDir().getPath() +  File.separator + outputFileName);
      flexConfig.addLibraryPath(PathUtils.getColtSWCPath());
      flexConfig.setOutputPath(PathUtils.getIncrementalSWCPath(project));
      
      // Load root module link report file for externs for Live-Coding
      // incremental module
      flexConfig.setLoadExternsFilePath(getLinkReportFilePath());

      // CO-4487 - compiler shenanigans for incremental mode      
      flexConfig.setVerifyDigests(false);
      flexConfig.setWarnings(false);
      flexConfig.setIncremental(true);
      
      // Changed source files must be copied to a separate folder and added to the config
      File incrementalSourcesDir = project.getOrCreateIncrementalSourcesDir();
      if (!assetsUpdateMode) {
        FileUtils.clear(incrementalSourcesDir);
        for (SourceFile sourceFile : changedFiles) {
          try {
            FileUtils.copyFileChecked(sourceFile.getFile(), new File(incrementalSourcesDir, sourceFile.getRelativePath()), false);
          } catch (IOException e) {
            throw new LCSException("Can't copy changed source file to 'incremental' dir", e);
          }
        }
      }      
      // Copy assets to the incremental source dir
      for (String sourcePath : project.getSourceSettings().getSourcePaths()) {
        File sourceDir = new File(sourcePath);
        if (sourceDir.exists()) {
          List<File> nonSources = FileUtils.listFileRecursively(sourceDir, new FileFilter() {            
            @Override
            public boolean accept(File file) {
              if (file.isDirectory()) {
                return false;
              }
              String fileNameLowerCase = file.getName().toLowerCase();
              return !fileNameLowerCase.endsWith(".as") && !fileNameLowerCase.endsWith(".mxml");
            }
          });
          for (File nonSource : nonSources) {
            String relativePath = FileUtils.getRelativePath(nonSource.getPath(), sourceDir.getPath(), File.separator);
            try {
              File newFile = new File(incrementalSourcesDir, relativePath);
              if (!newFile.getParentFile().exists()) {
                newFile.getParentFile().mkdirs();
              }
              FileUtils.copyFileChecked(nonSource, newFile, false);
            } catch (IOException e) {
              throw new LCSException("Can't copy file to 'incremental' dir", e);
            }
          }
        }
      }
      flexConfig.addSourcePath(incrementalSourcesDir.getPath());
    }

    // Link report file generation
    if (incrementalCompilation) {
      flexConfig.setLinkReportFilePath(null);      
    } else {
      flexConfig.setLinkReportFilePath(getLinkReportFilePath());
    }

    // Output path
    if (!incrementalCompilation) {
      flexConfig.setOutputPath(project.getOutputDir().getPath() + File.separator + compilerSettings.getOutputFilename());
    }

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

  public static void addLibraryClasses(FlexConfig flexConfig, List<String> sourcePaths) {
    for (String sourcePath : sourcePaths) {
      File sourceDir = new File(sourcePath);
      if (!sourceDir.exists() || !sourceDir.isDirectory()) {
        continue;
      }

      List<File> sourceFiles = FileUtils.listFileRecursively(sourceDir, new FileFilter() {
        @Override
        public boolean accept(File file) {
          String filenameLowerCase = file.getName().toLowerCase();
          return filenameLowerCase.endsWith(SourceFile.DOT_AS) || filenameLowerCase.endsWith(SourceFile.DOT_MXML);
        }
      });

      for (File sourceFile : sourceFiles) {
        String relativePath = FileUtils.getRelativePath(sourceFile.getPath(), sourceDir.getPath(), File.separator);

        if (relativePath.toLowerCase().endsWith(SourceFile.DOT_AS)) {
          relativePath = relativePath.substring(0, relativePath.length() - SourceFile.DOT_AS.length());
        } else if (relativePath.toLowerCase().endsWith(SourceFile.DOT_MXML)) {
          relativePath = relativePath.substring(0, relativePath.length() - SourceFile.DOT_MXML.length());
        }
        
        if (!StringUtils.isEmpty(relativePath)) {
          String fqName = NameUtil.namespaceFromPath(relativePath);
          
          if (!IgnoredSources.isIgnoredTrait(fqName)) {
            flexConfig.addClass(fqName);
          }
        }
      }
    }
  }

  private String getLinkReportFilePath() {
    return new File(project.getOutputDir(), "link-report.xml").getPath();
  }

}
