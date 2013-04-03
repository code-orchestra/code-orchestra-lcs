package codeOrchestra.actionScript.compiler.fcsh;

import codeOrchestra.lcs.flex.FlexSDKSettings;
import codeOrchestra.utils.process.JavaLauncher;

import com.intellij.openapi.util.SystemInfo;

/**
 * @author Alexander Eliseyev
 */
public class FCSHLauncher extends JavaLauncher {

  public FCSHLauncher() {
    super(null);
    
    StringBuilder programParameters = new StringBuilder();
    String flexHome = FlexSDKSettings.getDefaultFlexSDKPath();
    
    programParameters.append(protect("-Dapplication.home=" + flexHome));
    programParameters.append(" -Duser.language=en");
    programParameters.append(" -Duser.country=US");
    programParameters.append(" -Djava.awt.headless=true");
    programParameters.append(" -jar ");
    programParameters.append(protect(flexHome + "/liblc/fcsh.jar"));
    
    setProgramParameter(programParameters.toString());

    StringBuilder jvmParameters = new StringBuilder();
    jvmParameters.append("-Xmx384m -Dsun.io.useCanonCaches=false ");
    if (!SystemInfo.isWindows) {
      jvmParameters.append("-d32 ");
    }
    setVirtualMachineParameter(jvmParameters.toString());
  }

}
