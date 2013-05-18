package codeOrchestra.lcs.digest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import codeOrchestra.actionScript.flexsdk.FlexSDKLib;
import codeOrchestra.lcs.logging.Logger;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.utils.XMLUtils;

/**
 * @author Alexander Eliseyev
 */
public class ProjectDigestHelper {

  private final static Logger LOG = Logger.getLogger(ProjectDigestHelper.class);
  
  private final LCSProject project;

  public ProjectDigestHelper(LCSProject project) {
    this.project = project;    
  }
  
  public List<EmbedDigest> getEmbedDigests() {
    List<EmbedDigest> result = new ArrayList<EmbedDigest>();
    
    File embedDigestsFile = new File(project.getDigestsDir(), "embedDigests.xml");
    if (!embedDigestsFile.exists()) {
      LOG.error("Embed digests report file expected at " + embedDigestsFile.getPath() + " doesn't exist");
      return result;
    }
    
    Document document = XMLUtils.fileToDOM(embedDigestsFile);
    Element rootElement = document.getDocumentElement();
    
    NodeList embedElements = rootElement.getElementsByTagName("embed");
    if (embedElements != null) {
      for (int i = 0; i < embedElements.getLength(); i++) {
        Element embedElement = (Element) embedElements.item(i);
        result.add(new EmbedDigest(embedElement.getAttribute("source"), embedElement.getAttribute("mimeType"), embedElement.getAttribute("fullPath")));
      }
    }
    
    return result;
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
