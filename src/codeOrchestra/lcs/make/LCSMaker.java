package codeOrchestra.lcs.make;

import java.io.File;
import java.util.List;

import codeOrchestra.actionScript.compiler.fcsh.FCSHException;
import codeOrchestra.actionScript.compiler.fcsh.FCSHFlexSDKRunner;
import codeOrchestra.actionScript.compiler.fcsh.FCSHManager;
import codeOrchestra.actionScript.compiler.fcsh.FSCHCompilerKind;
import codeOrchestra.actionScript.compiler.fcsh.MaximumCompilationsCountReachedException;
import codeOrchestra.actionScript.compiler.fcsh.console.command.impl.LivecodingStartCommand;
import codeOrchestra.actionScript.compiler.fcsh.console.command.impl.LivecodingStopCommand;
import codeOrchestra.actionScript.make.ASMakeType;
import codeOrchestra.actionScript.modulemaker.CompilationResult;
import codeOrchestra.actionScript.modulemaker.MakeException;
import codeOrchestra.lcs.LCSException;
import codeOrchestra.lcs.flex.config.FlexConfig;
import codeOrchestra.lcs.flex.config.FlexConfigBuilder;
import codeOrchestra.lcs.logging.Logger;
import codeOrchestra.lcs.project.CompilerSettings;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.sources.SourceFile;

/** 
 * @author Alexander Eliseyev
 */
public class LCSMaker {

  private static final Logger LOG = Logger.getLogger(LCSMaker.class.getSimpleName());

  private static boolean sentLiveCodingCommand;
  private boolean isIncremental;
  private List<SourceFile> changedFiles;
  private boolean assetMode;
  
  private boolean skipSecondPhase;
  private boolean productionMode;
  
  public LCSMaker(boolean isIncremental) {
    this.isIncremental = isIncremental;
  }

  public LCSMaker(List<SourceFile> changedFilesSnapshot) {
    this(changedFilesSnapshot, false);
  }

  public LCSMaker(List<SourceFile> changedFilesSnapshot, boolean assetMode) {
    this(true);
    this.changedFiles = changedFilesSnapshot;
    this.assetMode = assetMode;
  }
  
  public void setProductionMode(boolean productionMode) {
    if (productionMode) {
      setSkipSecondPhase(true);
    }
    this.productionMode = productionMode;
  }

  public void setSkipSecondPhase(boolean skipSecondPhase) {
    this.skipSecondPhase = skipSecondPhase;
  }
  
  public CompilationResult make() throws MakeException, MaximumCompilationsCountReachedException {
    FSCHCompilerKind compilerKind = productionMode ? FSCHCompilerKind.MXMLC : (
        assetMode ? FSCHCompilerKind.COMPC : (
            isIncremental ? FSCHCompilerKind.INCREMENTAL_COMPC : FSCHCompilerKind.BASE_MXMLC
        )
    );

    LCSProject currentProject = LCSProject.getCurrentProject();
    CompilerSettings compilerSettings = currentProject.getCompilerSettings();

    // Generate & save Flex config
    FlexConfigBuilder flexConfigBuilder = new FlexConfigBuilder(currentProject, isIncremental, changedFiles, assetMode);
    FlexConfig flexConfig = null;
    File flexConfigFile = null;
    try {
      flexConfig = flexConfigBuilder.build();
    } catch (LCSException e) {
      throw new MakeException("Can't build a flex config", e);
    }
    try {
      flexConfigFile = flexConfig.saveToFile(currentProject.getFlexConfigPath(currentProject, compilerKind));
    } catch (LCSException e) {
      throw new MakeException("Can't save a flex config", e);
    }

    // Toggle livecoding mode in fcsh
    if (!productionMode && !assetMode) {
      if (isIncremental) {
        if (!sentLiveCodingCommand) {
          try {
            FCSHManager fcshManager = FCSHManager.instance();
            fcshManager.submitCommand(new LivecodingStartCommand());
          } catch (FCSHException e) {
            throw new MakeException("Unable to start livecoding mode in FCSH", e);
          }
          sentLiveCodingCommand = true;
        }
      } else {
        if (sentLiveCodingCommand) {
          try {
            FCSHManager fcshManager = FCSHManager.instance();
            fcshManager.submitCommand(new LivecodingStopCommand());
          } catch (FCSHException e) {
            throw new MakeException("Unable to stop livecoding mode in FCSH", e);
          }
          sentLiveCodingCommand = false;
        }
      }
    }

    // Custom SDK config file
    if (compilerSettings.useCustomSDKConfiguration()) {
      File customConfigFile = new File(compilerSettings.getCustomConfigPath());
      if (!customConfigFile.exists()) {
        throw new MakeException("Custom compile configuration file [" + compilerSettings.getCustomConfigPath() + "] doesn't exist");
      }
    }

    // Base/incremental compilation first phase
    FCSHFlexSDKRunner flexSDKRunner = getFlexSDKRunner(flexConfigFile, compilerKind);
    CompilationResult compilationResult = doCompile(flexSDKRunner);
    if (!compilationResult.isOk()) {
      return compilationResult;
    }

    // Base compilation second phase
    if (!isIncremental && !skipSecondPhase) {
      compilerKind = FSCHCompilerKind.BASE_COMPC;

      flexConfig.setOutputPath(flexConfig.getOutputPath().replaceFirst("\\.swf$", ".swc"));
      flexConfig.setLinkReportFilePath(null);
      flexConfig.setLibrary(true);

      FlexConfigBuilder.addLibraryClasses(flexConfig, currentProject.getSourceSettings().getSourcePaths());

      flexConfigFile.delete();
      try {
        flexConfigFile = flexConfig.saveToFile(currentProject.getFlexConfigPath(currentProject, compilerKind));
      } catch (LCSException e) {
        throw new MakeException("Can't save a flex config", e);
      }

      flexSDKRunner = getFlexSDKRunner(flexConfigFile, compilerKind);
      compilationResult = doCompile(flexSDKRunner);
      if (!compilationResult.isOk()) {
        return compilationResult;
      }
    }

    return compilationResult;
  }

  private CompilationResult doCompile(FCSHFlexSDKRunner flexSDKRunner) throws MakeException, MaximumCompilationsCountReachedException {
    CompilationResult compilationResult = flexSDKRunner.run();

    if (compilationResult == null) {
      String errorMessage = String.format("Compilation timed out");
      LOG.error(errorMessage);
      return CompilationResult.ABORTED;      
    }
    
    if (compilationResult.getErrors() > 0) {
      final String outputFile = flexSDKRunner.getErrorLogFilePath();
      String errorMessage = String.format("Compilation failed with (%d) error(s): %s", compilationResult.getErrors(), outputFile);

      LOG.error(errorMessage);
      return compilationResult;
    }

    LOG.info("Compilation is completed successfully");
    return compilationResult;
  }

  private FCSHFlexSDKRunner getFlexSDKRunner(File flexConfigFile, FSCHCompilerKind compilerKind) {
    return new FCSHFlexSDKRunner(flexConfigFile, ASMakeType.REGULAR, compilerKind);
  }

}
