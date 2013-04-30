package codeOrchestra.actionScript.compiler.fcsh.console.command.impl;

import codeOrchestra.actionScript.compiler.fcsh.console.command.AbstractCommandCallback;
import codeOrchestra.actionScript.compiler.fcsh.console.command.CommandOutput;

/**
 * @author Alexander Eliseyev
 */
public class CPUProfilingStartCommand extends AbstractCommandCallback {
  @Override
  public String getCommand() {
    return "profiling.cpu.start";
  }

  @Override
  public void done(CommandOutput response) {
    // do nothing
  }

  @Override
  public boolean isSynchronous() {
    return true;
  }
}
