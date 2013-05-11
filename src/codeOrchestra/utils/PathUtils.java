package codeOrchestra.utils;

import java.io.File;

import org.eclipse.core.runtime.Platform;

import codeOrchestra.lcs.project.LCSProject;

/**
 * @author Alexander Eliseyev
 */
public class PathUtils {
  
  public static String getFlexSDKPath() {
    File productDir = getApplicationBaseDir();    
    return new File(productDir, "flex_sdk").getPath();
  }
  
  public static String getTemplaesDir() {
    File productDir = getApplicationBaseDir();    
    return new File(productDir, "templates").getPath();
  }
  
  public static String getColtSWCPath() {
    File productDir = getApplicationBaseDir();    
    return new File(new File(productDir, "lib"), "colt.swc").getPath();
  }
  
  public static String getIncrementalSWFPath(LCSProject project, int packageId) {
    return getIncrementalOutputDir(project) +  File.separator + "package_" + packageId + ".swf";
  }
  
  public static String getIncrementalOutputDir(LCSProject project) {
    return project.getOutputDir().getPath() + File.separator + "livecoding";
  }

  public static String getIncrementalSWCPath(LCSProject project) {
    return project.getOutputDir().getPath() +  File.separator + project.getName() + "_liveCoding.swc";
  }
  
  public static File getApplicationBaseDir() {
	//TODO: not working on Windows, if "C:/flex_sdk" directory exist!
    // Try the bundle path (deployed product)
    File dir = Platform.getProduct().getDefiningBundle().getDataFile("tmp").getParentFile();
    while ((dir = dir.getParentFile()) != null) {
      File possibleFlexSDKDir = new File(dir, "flex_sdk");
      if (possibleFlexSDKDir.exists()) {
        return dir;
      }
    }
    
    // Try the product location (workbench)
    String installLocation = Platform.getInstallLocation().getURL().getPath();
    String productLocationURL = Platform.getProduct().getDefiningBundle().getLocation();
    
    String relativePath = productLocationURL;
    
    if (relativePath.contains("initial@")) {
    	relativePath = relativePath.replace("initial@reference:file:", "");
	    File productDir = new File(installLocation);
	    int parentMarksCount = StringUtils.countMatches(relativePath, "..");
	    for (int i = 0; i < parentMarksCount; i++) {
	      productDir = productDir.getParentFile();
	    }
	
	    if (parentMarksCount > 0) {
	      relativePath = relativePath.substring(StringUtils.lastIndexOf(relativePath, "..") + 3);
	    }
	    return new File(productDir, relativePath);
    } else {
    	relativePath = relativePath.replace("reference:file:", "");
    	return new File(relativePath);
    }
    
  }

}
