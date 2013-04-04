package codeOrchestra.lcs.sources;

import java.io.File;

import codeOrchestra.utils.FileUtils;
import codeOrchestra.utils.NameUtil;
import codeOrchestra.utils.StringUtils;

public class SourceFile {

  public static final String DOT_MXML = ".mxml";
  public static final String DOT_AS = ".as";
  
  private File file;
  private String fqName;
  private String relativePath;
  
  public SourceFile(File file, String sourceDir) {
    this.file = file;
    
    relativePath = FileUtils.getRelativePath(file.getPath(), sourceDir, File.separator);    
    
    if (!StringUtils.isEmpty(relativePath)) {
      if (relativePath.toLowerCase().endsWith(DOT_AS)) {
        fqName = NameUtil.namespaceFromPath(relativePath.substring(0, relativePath.length() - DOT_AS.length()));
      } else if (relativePath.toLowerCase().endsWith(DOT_MXML)) {
        fqName = NameUtil.namespaceFromPath(relativePath.substring(0, relativePath.length() - DOT_MXML.length()));
      } else {
        fqName = NameUtil.namespaceFromPath(relativePath);        
      }
    } else {
      throw new IllegalArgumentException(file.getPath());
    }
  }
  
  public File getFile() {
    return file;
  }
  
  public String getRelativePath() {
    return relativePath;
  }
  
  public String getFqName() {
    return fqName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((file == null) ? 0 : file.hashCode());
    result = prime * result + ((fqName == null) ? 0 : fqName.hashCode());
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
    SourceFile other = (SourceFile) obj;
    if (file == null) {
      if (other.file != null)
        return false;
    } else if (!file.equals(other.file))
      return false;
    if (fqName == null) {
      if (other.fqName != null)
        return false;
    } else if (!fqName.equals(other.fqName))
      return false;
    return true;
  }
  
  
  
}
