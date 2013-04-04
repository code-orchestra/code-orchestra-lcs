package codeOrchestra.actionScript.compiler.fcsh.console.command.impl;

import codeOrchestra.actionScript.compiler.fcsh.FCSHManager;

import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public class LivecodingBaseMXMLCCommand extends AbstractCompileWithArgumentsCommandCallback {

  public static final String EXECUTABLE_NAME = "lcmxmlc";

  public LivecodingBaseMXMLCCommand(FCSHManager fcshManager, List<String> arguments) {
    super(fcshManager, arguments);
  }

  @Override
  protected String getExecutableName() {
    return EXECUTABLE_NAME;
  }

}
