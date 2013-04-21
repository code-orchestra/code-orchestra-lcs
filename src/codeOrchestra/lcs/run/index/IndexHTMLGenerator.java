package codeOrchestra.lcs.run.index;

import java.io.File;
import java.io.IOException;

import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.utils.FileUtils;
import codeOrchestra.utils.PathUtils;

public class IndexHTMLGenerator {

  private LCSProject project;

  public IndexHTMLGenerator(LCSProject project) {
    this.project = project;
  }

  public void generate() throws IOException {
    File targetSWFObjectFile = new File(project.getOutputDir(), "swfobject.js");
    FileUtils.copyFileChecked(new File(PathUtils.getTemplaesDir(), "swfobject.js"), targetSWFObjectFile, false);
    
    File targetIndexFile = new File(project.getOutputDir(), "index.html");
    FileUtils.copyFileChecked(new File(PathUtils.getTemplaesDir(), "index.html"), targetIndexFile, false);
    
    String indexContent = FileUtils.read(targetIndexFile);
    indexContent = indexContent.replace("{SWF_NAME}", project.getCompilerSettings().getOutputFilename());
    FileUtils.write(targetIndexFile, indexContent);
  }
  
}
