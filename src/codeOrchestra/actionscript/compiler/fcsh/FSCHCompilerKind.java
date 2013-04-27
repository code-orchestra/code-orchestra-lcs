package codeOrchestra.actionScript.compiler.fcsh;

/**
 * @author Alexander Eliseyev
 */
public enum FSCHCompilerKind {
  BASE_MXMLC("lcmxmlc"),
  BASE_COMPC("lccompc"),
  INCREMENTAL_COMPC("lcicompc"), 
  COMPC("compc");
  
  private String commandName;

  private FSCHCompilerKind(String commandName) {
    this.commandName = commandName;
  }

  public String getCommandName() {
    return commandName;
  }
  
}
