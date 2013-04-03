package codeOrchestra.actionScript.compiler.fcsh.console.command.impl;

import codeOrchestra.actionScript.compiler.fcsh.FCSHManager;

import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public class LivecodingBaseMXMLCCommand extends AbstractCompileWithArgumentsCommandCallback {

  public LivecodingBaseMXMLCCommand(FCSHManager fcshManager, List<String> arguments) {
    super(fcshManager, arguments);
  }

  @Override
  protected String getExecutableName() {
    return "lcmxmlc";
  }

}
