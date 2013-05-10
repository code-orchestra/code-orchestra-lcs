package codeOrchestra.lcs.flex.usedCode;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.sources.IgnoredSources;
import codeOrchestra.lcs.sources.SourceFile;
import codeOrchestra.utils.FileUtils;
import codeOrchestra.utils.NameUtil;
import codeOrchestra.utils.StringUtils;

public class VisibleClassesCollector {
  
  private LCSProject project;

  public VisibleClassesCollector(LCSProject project) {
    this.project = project;
  }
  
  public List<String> getVisibleSourceClassesFqNames() {
    List<String> result = new ArrayList<String>();    
    addLibraryClasses(project.getSourceSettings().getSourcePaths(), result);    
    return result;
  }
  
  public static void addLibraryClasses(List<String> sourcePaths, List<String> result) {
    for (String sourcePath : sourcePaths) {
      File sourceDir = new File(sourcePath);
      if (!sourceDir.exists() || !sourceDir.isDirectory()) {
        continue;
      }

      List<File> sourceFiles = FileUtils.listFileRecursively(sourceDir, new FileFilter() {
        @Override
        public boolean accept(File file) {
          String filenameLowerCase = file.getName().toLowerCase();
          return filenameLowerCase.endsWith(SourceFile.DOT_AS) || filenameLowerCase.endsWith(SourceFile.DOT_MXML);
        }
      });

      for (File sourceFile : sourceFiles) {
        String relativePath = FileUtils.getRelativePath(sourceFile.getPath(), sourceDir.getPath(), File.separator);

        if (relativePath.toLowerCase().endsWith(SourceFile.DOT_AS)) {
          relativePath = relativePath.substring(0, relativePath.length() - SourceFile.DOT_AS.length());
        } else if (relativePath.toLowerCase().endsWith(SourceFile.DOT_MXML)) {
          relativePath = relativePath.substring(0, relativePath.length() - SourceFile.DOT_MXML.length());
        }
        
        if (!StringUtils.isEmpty(relativePath)) {
          String fqName = NameUtil.namespaceFromPath(relativePath);
          
          if (!IgnoredSources.isIgnoredTrait(fqName)) {
            result.add(fqName);
          }
        }
      }
    }
  }
  

}
