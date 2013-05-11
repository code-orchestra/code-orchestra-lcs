package codeOrchestra.actionScript.compiler.fcsh.console.command.impl;

import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public class LivecodingIncrementalCOMPCCommand extends AbstractCompileWithArgumentsCommandCallback {

  public static final String EXECUTABLE_NAME = "lcicompc";
  
    public LivecodingIncrementalCOMPCCommand(List<String> arguments) {
    super(arguments);
  }

  @Override
  protected String getExecutableName() {
    return EXECUTABLE_NAME;
  }
  
}
