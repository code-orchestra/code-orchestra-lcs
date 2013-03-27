package codeOrchestra.actionscript.compiler.fcsh.console.command.impl;

import codeOrchestra.actionscript.compiler.fcsh.FCSHManager;
import codeOrchestra.actionscript.compiler.fcsh.console.command.CommandOutput;
import codeOrchestra.actionscript.compiler.fcsh.target.CompilerTarget;
import codeOrchestra.actionscript.modulemaker.CompilationResult;

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
      if (response.getNormalOutput().contains(getTargetNotFoundMessage())) {
        fcshManager.clearTargets();
        log.error("Compilation target specified not found, try rebuilding");
        return new CompilationResult(1, 0, false);
      }
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
