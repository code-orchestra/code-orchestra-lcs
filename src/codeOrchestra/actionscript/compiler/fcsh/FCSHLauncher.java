package codeOrchestra.actionScript.compiler.fcsh;

import java.io.File;

import codeOrchestra.lcs.flex.FlexSDKSettings;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.project.LiveCodingSettings;
import codeOrchestra.utils.process.JavaLauncher;

import com.intellij.openapi.util.SystemInfo;

/**
 * @author Alexander Eliseyev
 */
public class FCSHLauncher extends JavaLauncher {

  public FCSHLauncher() {
    super(null);

    StringBuilder programParameters = new StringBuilder();

    String applicationHome;
    LCSProject currentProject = LCSProject.getCurrentProject();
    if (currentProject != null) {
      applicationHome = currentProject.getCompilerSettings().getFlexSDKPath();
      if (!new File(applicationHome).exists()) {
        applicationHome = FlexSDKSettings.getDefaultFlexSDKPath();
      }
    } else {
      applicationHome = FlexSDKSettings.getDefaultFlexSDKPath();
    }

    programParameters.append(protect("-Dapplication.home=" + applicationHome));
    programParameters.append(" -Duser.language=en");
    programParameters.append(" -Duser.country=US");
    programParameters.append(" -Djava.awt.headless=true");

    // Livecoding parameters
    if (currentProject != null) {
      LiveCodingSettings liveCodingSettings = currentProject.getLiveCodingSettings();
      programParameters.append(" -DcodeOrchestra.live.liveMethods=" + liveCodingSettings.getLiveMethods().getPreferenceValue());
      programParameters.append(" -DcodeOrchestra.live.gettersSetters=" + liveCodingSettings.makeGettersSettersLive());
      programParameters.append(" -DcodeOrchestra.live.maxLoops=" + liveCodingSettings.getMaxIterationsCount());
      programParameters.append(" -DcodeOrchestra.digestsDir=" + protect(currentProject.getDigestsDir().getPath()));
    }

    programParameters.append(" -jar ");
    programParameters.append(protect(FlexSDKSettings.getDefaultFlexSDKPath() + "/liblc/fcsh.jar"));

    setProgramParameter(programParameters.toString());

    StringBuilder jvmParameters = new StringBuilder();
    jvmParameters.append("-Xmx384m -Dsun.io.useCanonCaches=false ");
    if (!SystemInfo.isWindows) {
      jvmParameters.append("-d32 ");
    }
    setVirtualMachineParameter(jvmParameters.toString());
  }

}
