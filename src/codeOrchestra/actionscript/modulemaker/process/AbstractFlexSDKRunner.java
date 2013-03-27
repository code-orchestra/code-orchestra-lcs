package codeOrchestra.actionScript.modulemaker.process;

import codeOrchestra.actionScript.make.ASMakeType;
import codeOrchestra.actionScript.modulemaker.CompilationResult;
import codeOrchestra.actionScript.modulemaker.MakeException;
import codeOrchestra.lcs.flex.FlexSDKSettings;
import codeOrchestra.lcs.project.CompilerSettings;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.utils.StringUtils;

import org.apache.tools.ant.types.Commandline;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public abstract class AbstractFlexSDKRunner {

  private static final String DEFAULT_CONFIG_FILE_NAME = "flex-config.xml";
  private static final String DEFAULT_CONFIG_FILE_DIR = "frameworks";

  protected File configFile;
  protected ASMakeType moduleMakeType;  
  protected CompilerSettings compilerSettings;

  public AbstractFlexSDKRunner(File configFile, ASMakeType moduleMakeType) {
    this.configFile = configFile;
    this.moduleMakeType = moduleMakeType;
    this.compilerSettings = LCSProject.getCurrentProject().getCompilerSettings();
  }

  public abstract CompilationResult run() throws MakeException;

  public abstract String getErrorLogFilePath();

  protected abstract String getCommandName();

  protected List<String> getCommandArguments() {
    List<String> commandArguments = new ArrayList<String>();

    boolean firstConfigSet = false;

    // Default SDK configuration file
    if (compilerSettings.useDefaultSDKConfiguration()) {
      String defaultConfigFileArg = "-load-config=" + getDefaultConfigurationFilePath();
      commandArguments.add(defaultConfigFileArg);

      firstConfigSet = true;
    }

    // Custom configuration file
    if (compilerSettings.useCustomSDKConfiguration()) {
      String customConfigFileArg = "-load-config" + (firstConfigSet ? "+=" : "=") + compilerSettings.getCustomConfigPath();
      commandArguments.add(customConfigFileArg);
    } else {
      // Module configuration file
      String configFileArg = "-load-config" + (firstConfigSet ? "+=" : "=") + configFile.getPath();
      commandArguments.add(configFileArg);
    }

    // Additional compiler options
    if (!StringUtils.isEmpty(compilerSettings.getCompilerOptions())) {
      String[] additionalArgs = new Commandline("commandtoken " + compilerSettings.getCompilerOptions()).getArguments();
      for (String additionalArgument : additionalArgs) {
        commandArguments.add(additionalArgument);
      }

      for (String moduleMakeTypeArgument : moduleMakeType.getAdditionalCompilerArgs()) {
        commandArguments.add(moduleMakeTypeArgument);
      }
    }

    return commandArguments;
  }

  public static String protect(String result) {
    if (result.contains(" ")) {
      return "\"" + result + "\"";
    }
    return result;
  }

  private static String getDefaultConfigurationFilePath() {
    String flexSDKPath = FlexSDKSettings.getDefaultFlexSDKPath();
    return flexSDKPath + File.separator + DEFAULT_CONFIG_FILE_DIR + File.separator + DEFAULT_CONFIG_FILE_NAME;
  }

}
