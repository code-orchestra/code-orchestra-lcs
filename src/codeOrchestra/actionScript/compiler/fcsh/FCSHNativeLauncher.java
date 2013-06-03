package codeOrchestra.actionScript.compiler.fcsh;

import java.io.File;

import codeOrchestra.utils.PathUtils;

/**
 * @author Alexander Eliseyev
 */
public class FCSHNativeLauncher implements IFCSHLauncher {
	
	public ProcessBuilder createProcessBuilder() {
		ProcessBuilder processBuilder = new ProcessBuilder(PathUtils.getFlexSDKPath() + File.separator + "bin" + File.separator + "ColtFCSH.exe");
		
		
		
		return processBuilder;
	}

	@Override
	public void runBeforeStart() {
		// TODO Auto-generated method stub
		
	}

}
