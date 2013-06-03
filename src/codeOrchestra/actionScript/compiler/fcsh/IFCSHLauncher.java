package codeOrchestra.actionScript.compiler.fcsh;

/**
 * @author Alexander Eliseyev
 */
public interface IFCSHLauncher {

	ProcessBuilder createProcessBuilder();
	
	void runBeforeStart();
	
}
