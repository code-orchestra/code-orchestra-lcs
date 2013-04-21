package codeOrchestra.lcs.digest;

public class Member {

  private String name;
  private boolean isStatic;
  
  public Member(String name, boolean isStatic) {
    this.name = name;
    this.isStatic = isStatic;
  }

  public String getName() {
    return name;
  }

  public boolean isStatic() {
    return isStatic;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (isStatic ? 1231 : 1237);
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    Member other = (Member) obj;
    if (isStatic != other.isStatic)
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }
  
  
  
}