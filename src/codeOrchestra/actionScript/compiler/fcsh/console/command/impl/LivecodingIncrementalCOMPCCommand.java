package codeOrchestra.actionScript.compiler.fcsh.console.command.impl;

import java.util.List;

import codeOrchestra.actionScript.compiler.fcsh.FCSHManager;

/**
 * @author Alexander Eliseyev
 */
public class LivecodingIncrementalCOMPCCommand extends AbstractCompileWithArgumentsCommandCallback {

  public static final String EXECUTABLE_NAME = "lcicompc";
  
    public LivecodingIncrementalCOMPCCommand(FCSHManager fcshManager, List<String> arguments) {
    super(fcshManager, arguments);
  }

  @Override
  protected String getExecutableName() {
    return EXECUTABLE_NAME;
  }
  
}
