package codeOrchestra.actionscript.compiler.fcsh.console.command.impl;

import codeOrchestra.actionscript.compiler.fcsh.FCSHManager;

import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public class COMPCCommand extends AbstractCompileWithArgumentsCommandCallback {

  public COMPCCommand(FCSHManager fcshManager, List<String> arguments) {
    super(fcshManager, arguments);
  }

  @Override
  protected String getExecutableName() {
    return "compc";
  }

}
