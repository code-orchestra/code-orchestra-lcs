package codeOrchestra.lcs.digest;

import java.util.ArrayList;
import java.util.List;

import codeOrchestra.actionScript.flexsdk.FlexSDKLib;
import codeOrchestra.lcs.logging.Logger;
import codeOrchestra.lcs.project.LCSProject;

public class ProjectDigestHelper {

  private final static Logger LOG = Logger.getLogger(ProjectDigestHelper.class);
  
  private final LCSProject project;

  public ProjectDigestHelper(LCSProject project) {
    this.project = project;    
  }
  
  public void build() {
    long timeStarted = System.currentTimeMillis();
    
    List<String> swcPaths = new ArrayList<String>();

    for (FlexSDKLib flexLib : FlexSDKLib.values()) {
      String swcPath = flexLib.getPath();
      if (swcPath != null) {
        swcPaths.add(swcPath);
      }
    }
    
    for (String projectLibPath : project.getSourceSettings().getLibraryPaths()) {
      swcPaths.add(projectLibPath);
    }
    
    SWCDigest swcDigest = new SWCDigest(swcPaths, project.getDigestsDir().getPath());
    swcDigest.generate();
    
    LOG.info("Digests building took " + (System.currentTimeMillis() - timeStarted) + "ms");
  }
  
}
