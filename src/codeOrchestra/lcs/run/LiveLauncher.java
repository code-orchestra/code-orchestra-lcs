package codeOrchestra.lcs.run;

import java.io.File;
import java.io.FilenameFilter;

import jetbrains.mps.execution.api.commands.ProcessHandlerBuilder;

import codeOrchestra.actionScript.run.BrowserUtil;
import codeOrchestra.actionScript.security.TrustedLocations;
import codeOrchestra.lcs.air.ipaBuildScriptGenerator;
import codeOrchestra.lcs.project.CompilerSettings;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.project.LiveCodingSettings;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.util.SystemInfo;

/**
 * @author Alexander Eliseyev
 */
public class LiveLauncher {

  public ProcessHandler launch(LCSProject project) throws ExecutionException {
    LiveCodingSettings liveCodingSettings = project.getLiveCodingSettings();
    CompilerSettings compilerSettings = project.getCompilerSettings();
    Target launchTarget = liveCodingSettings.getLaunchTarget();

    if (launchTarget == Target.AIR) {
      return new ProcessHandlerBuilder().append(protect(ipaBuildScriptGenerator.getSctiptPath(project).getPath())).build(project.getOutputDir());
    }
    
    LauncherType launcherType = liveCodingSettings.getLauncherType();    
    String swfPath = compilerSettings.getOutputPath() + File.separator + compilerSettings.getOutputFilename();    
    if (launchTarget == Target.SWF) {
      TrustedLocations.getInstance().addTrustedLocation(swfPath);
    }
    
    String target = launchTarget == Target.WEB_ADDRESS ? liveCodingSettings.getWebAddress() : swfPath;

    switch (launcherType) {
    case DEFAULT:
      return new ProcessHandlerBuilder().append(getCommand(BrowserUtil.launchBrowser(target, null))).build();
    case FLASH_PLAYER:
      return new ProcessHandlerBuilder().append(completeFlashPlayerPath(liveCodingSettings.getFlashPlayerPath())).append(protect(target)).build();
    default:
      throw new ExecutionException("Unsupported launcher type: " + launcherType);
    }
  }

  private String getCommand(ProcessBuilder processBuilder) {
    StringBuilder res = new StringBuilder();
    for (String s : processBuilder.command()) {
      res.append(s).append(" ");
    }
    return res.toString();
  }

  private String completeFlashPlayerPath(String playerPath) throws ExecutionException {
    File playerFile = new File(playerPath);
    if (!(playerFile.exists())) {
      throw new ExecutionException("Can't locate Flash Player under " + playerPath);
    }

    String result = playerPath;
    if (SystemInfo.isMac) {
      if (playerFile.isDirectory()) {
        File executableDir = new File(playerFile, "Contents/MacOS");
        if (!(executableDir.exists())) {
          throw new ExecutionException("Can't locate Flash Player under " + playerPath);
        }

        File[] files = executableDir.listFiles(new FilenameFilter() {
          public boolean accept(File file, String fileName) {
            return fileName.toLowerCase().contains("player");
          }

        });
        if (files == null || files.length == 0) {
          throw new ExecutionException("Can't locate Flash Player under " + playerPath);
        }

        result = files[0].getPath();
      }
    } else if (SystemInfo.isWindows) {
      // Do nothing
    } else {
      // Do nothing
    }

    return protect(result);
  }

  private String protect(String result) {
    if (result.contains(" ")) {
      return "\"" + result + "\"";
    }
    return result;
  }

}
