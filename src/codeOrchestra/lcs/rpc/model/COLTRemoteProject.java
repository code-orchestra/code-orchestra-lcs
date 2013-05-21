package codeOrchestra.lcs.rpc.model;

public class COLTRemoteProject {

  private String parentIDEProjectPath;
  
  private String path;
  private String name;
  
  private String[] sources;
  private String[] libraries;
  private String[] assets;
  
  private String htmlTemplateDir;
  
  
  
  public String getParentIDEProjectPath() {
    return parentIDEProjectPath;
  }

  public void setParentIDEProjectPath(String parentIDEProjectPath) {
    this.parentIDEProjectPath = parentIDEProjectPath;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String[] getSources() {
    return sources;
  }

  public void setSources(String[] sources) {
    this.sources = sources;
  }
  
}
