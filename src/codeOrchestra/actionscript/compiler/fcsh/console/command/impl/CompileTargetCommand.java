package codeOrchestra.actionScript.compiler.fcsh.console.command.impl;

import codeOrchestra.actionScript.compiler.fcsh.FCSHManager;
import codeOrchestra.actionScript.compiler.fcsh.console.command.CommandOutput;
import codeOrchestra.actionScript.compiler.fcsh.target.CompilerTarget;
import codeOrchestra.actionScript.modulemaker.CompilationResult;

/**
 * @author Alexander Eliseyev
 */
public class CompileTargetCommand extends AbstractCompileCommandCallback implements CompileCommandCallback {

  private FCSHManager fcshManager;
  private CompilerTarget target;

  public CompileTargetCommand(FCSHManager manager, CompilerTarget target) {
    fcshManager = manager;
    this.target = target;
  }

  @Override
  protected CompilationResult compile(CommandOutput response) {   
    if (response.getErrorOutput() == null) {      
      String normalOutput = response.getNormalOutput();

      if (normalOutput.contains(getTargetNotFoundMessage())) {
        fcshManager.clearTargets();
        log.error("Compilation target specified not found, try rebuilding");
        return new CompilationResult(1, 0, false);
      }
      
      //   Parse the compiler target
      String[] responseLines = normalOutput.split("\\r?\\n");
      processResponseAdditionally(responseLines);
    }

    return getCompilationResult(response);
  }

  private String getTargetNotFoundMessage() {
    return "Target " + target.getId() + " not found";
  }

  @Override
  public String getCommand() {
    return getExecutableName() + " " + target.getId();
  }

  @Override
  protected String getExecutableName() {
    return "compile";
  }

}
