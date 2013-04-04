package codeOrchestra.actionScript.compiler.fcsh.target;

import java.util.List;

public class CompilerCommand {

  private List<String> arguments;
  private String commandExecutable;

  public CompilerCommand(List<String> arguments, String commandExecutable) {
    this.arguments = arguments;
    this.commandExecutable = commandExecutable;
  }

  public List<String> getArguments() {
    return arguments;
  }

  public String getCommandExecutable() {
    return commandExecutable;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((arguments == null) ? 0 : arguments.hashCode());
    result = prime * result + ((commandExecutable == null) ? 0 : commandExecutable.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CompilerCommand other = (CompilerCommand) obj;
    if (arguments == null) {
      if (other.arguments != null)
        return false;
    } else if (!arguments.equals(other.arguments))
      return false;
    if (commandExecutable == null) {
      if (other.commandExecutable != null)
        return false;
    } else if (!commandExecutable.equals(other.commandExecutable))
      return false;
    return true;
  }

}
