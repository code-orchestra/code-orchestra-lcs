package codeOrchestra.actionScript.compiler.fcsh.target;

/**
 * @author Alexander Eliseyev
 */
public class CompilerTarget {

  private int id;

  public CompilerTarget(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
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
    CompilerTarget other = (CompilerTarget) obj;
    if (id != other.id)
      return false;
    return true;
  }
  
  

}
