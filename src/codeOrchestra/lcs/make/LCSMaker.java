package codeOrchestra.lcs.make;

import java.io.File;

import codeOrchestra.actionScript.compiler.fcsh.FCSHException;
import codeOrchestra.actionScript.compiler.fcsh.FCSHFlexSDKRunner;
import codeOrchestra.actionScript.compiler.fcsh.FCSHManager;
import codeOrchestra.actionScript.compiler.fcsh.FSCHCompilerKind;
import codeOrchestra.actionScript.compiler.fcsh.console.command.impl.LivecodingStartCommand;
import codeOrchestra.actionScript.make.ASMakeType;
import codeOrchestra.actionScript.modulemaker.CompilationResult;
import codeOrchestra.actionScript.modulemaker.MakeException;
import codeOrchestra.lcs.LCSException;
import codeOrchestra.lcs.flex.config.FlexConfig;
import codeOrchestra.lcs.flex.config.FlexConfigBuilder;
import codeOrchestra.lcs.logging.Logger;
import codeOrchestra.lcs.project.CompilerSettings;
import codeOrchestra.lcs.project.LCSProject;

public class LCSMaker {

  private static final Logger LOG = Logger.getLogger(LCSMaker.class.getSimpleName());
  
  private static boolean livecodingFCSH; // TODO: check usage
  
  private boolean isIncremental;
  
  public LCSMaker(boolean isIncremental) {
    this.isIncremental = isIncremental;
  }

  public void make() throws MakeException {
    LCSProject currentProject = LCSProject.getCurrentProject();
    CompilerSettings compilerSettings = currentProject.getCompilerSettings();
    
    // Generate & save Flex config    
    FlexConfigBuilder flexConfigBuilder = new FlexConfigBuilder(currentProject, false, false);
    FlexConfig flexConfig = null;
    File flexConfigFile = null;
    try {
      flexConfig = flexConfigBuilder.build();
    } catch (LCSException e) {
      throw new MakeException("Can't build a flex config", e);
    }
    try {
      flexConfigFile = flexConfig.saveToFile(currentProject.getFlexConfigPath(currentProject));
    } catch (LCSException e) {
      throw new MakeException("Can't save a flex config", e);
    }
    
    // Start livecoding mode in fcsh
    if (isIncremental && !livecodingFCSH) {
      try {
        FCSHManager fcshManager = FCSHManager.instance();
        fcshManager.submitCommand(new LivecodingStartCommand());
      } catch (FCSHException e) {
        throw new MakeException("Unable to start livecoding mode in FCSH", e);
      }
      livecodingFCSH = true;
    }
    
    // Custom SDK config file
    if (compilerSettings.useCustomSDKConfiguration()) {
      File customConfigFile = new File(compilerSettings.getCustomConfigPath());
      if (!customConfigFile.exists()) {
        throw new MakeException("Custom compile configuration file [" + compilerSettings.getCustomConfigPath() + "] doesn't exist");
      }
    }
    
    // Base/incremental compilation first phase
    FCSHFlexSDKRunner flexSDKRunner = getFlexSDKRunner(flexConfigFile, isIncremental ? FSCHCompilerKind.COMPC : FSCHCompilerKind.MXMLC);
    doCompile(flexSDKRunner);    
    
    // Base compilation second phase
    if (!isIncremental) {
      flexConfig.setOutputPath(flexConfig.getOutputPath().replaceFirst("\\.swf$", ".swc"));
      flexConfig.setLinkReportFilePath(null);
      flexConfig.setLibrary(true);   
      
      FlexConfigBuilder.addLibraryClasses(flexConfig, currentProject.getSourceSettings());
      
      flexConfigFile.delete();
      try {
        flexConfigFile = flexConfig.saveToFile(currentProject.getFlexConfigPath(currentProject));
      } catch (LCSException e) {
        throw new MakeException("Can't save a flex config", e);
      }
      
      FCSHManager.instance().clearTargets();

      flexSDKRunner = getFlexSDKRunner(flexConfigFile, FSCHCompilerKind.COMPC);
      doCompile(flexSDKRunner);
    }
  }

  private void doCompile(FCSHFlexSDKRunner flexSDKRunner) throws MakeException {
    long compilationStart = System.currentTimeMillis();
    CompilationResult compilationResult = flexSDKRunner.run();
    long compilationTook = System.currentTimeMillis() - compilationStart;

    if (compilationResult.getErrors() > 0) {
      final String outputFile = flexSDKRunner.getErrorLogFilePath();
      String errorMessage = String.format(
        "Compilation failed with (%d) error(s): %s",
        compilationResult.getErrors(),
        outputFile);

      LOG.error(errorMessage);
    } else {
      LOG.info("Compilation is completed successfully");
    }
    
    // TODO: log compilation time
  }

  private FCSHFlexSDKRunner getFlexSDKRunner(File flexConfigFile, FSCHCompilerKind compilerKind) {
    return new FCSHFlexSDKRunner(flexConfigFile, ASMakeType.REGULAR, compilerKind);
  }
  
}
