package codeOrchestra.lcs.air;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.utils.PathUtils;
import codeOrchestra.utils.TemplateCopyUtil;

/**
 * @author Alexander Eliseyev
 */
public class ipaBuildScriptGenerator {
  
  private LCSProject project;
  private String appName;

  public ipaBuildScriptGenerator(LCSProject project, String appName) {
    this.project = project;
    this.appName = appName;
  }
  
  public String generate() throws IOException {
    File targetScriptFile = getSctiptPath(project);
    File templateFile = new File(PathUtils.getTemplaesDir(), "ipaBuild.sh");

    Map<String, String> replacements = new HashMap<String, String>();
    replacements.put("{FLEX_SDK}", project.getCompilerSettings().getFlexSDKPath());
    replacements.put("{APPNAME}", appName);
    TemplateCopyUtil.copy(templateFile, targetScriptFile, replacements);
    
    targetScriptFile.setExecutable(true);
    
    return targetScriptFile.getPath();
  }

  public static File getSctiptPath(LCSProject project) {
    return new File(project.getOutputDir(), "ipaBuild.sh");
  }

}
