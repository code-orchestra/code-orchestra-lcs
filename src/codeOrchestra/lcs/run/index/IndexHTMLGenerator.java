package codeOrchestra.lcs.run.index;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.utils.FileUtils;
import codeOrchestra.utils.PathUtils;
import codeOrchestra.utils.TemplateProcessor;

/**
 * @author Alexander Eliseyev
 */
public class IndexHTMLGenerator {

  private LCSProject project;

  public IndexHTMLGenerator(LCSProject project) {
    this.project = project;
  }

  public void generate() throws IOException {
    File targetSWFObjectFile = new File(project.getOutputDir(), "swfobject.js");
    FileUtils.copyFileChecked(new File(PathUtils.getTemplatesDir(), "swfobject.js"), targetSWFObjectFile, false);
    
    File targetIndexFile = new File(project.getOutputDir(), "index.html");
    FileUtils.copyFileChecked(new File(PathUtils.getTemplatesDir(), "index.html"), targetIndexFile, false);
    
    Map<String, String> replacements = new HashMap<String, String>();
    replacements.put("{SWF_NAME}", project.getCompilerSettings().getOutputFilename());
    new TemplateProcessor(targetIndexFile, replacements).process();
  }
  
}
