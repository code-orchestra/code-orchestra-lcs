package codeOrchestra.utils;

import java.io.File;

import org.eclipse.core.runtime.Platform;

/**
 * @author Alexander Eliseyev
 */
public class PathUtils {
  
  public static String getFlexSDKPath() {
    File productDir = getApplicationBaseDir();    
    return new File(productDir, "flex_sdk").getPath();
  }
  
  public static String getActionScriptLibsSourcePath() {
    File productDir = getApplicationBaseDir();    
    return new File(productDir, "as_libs" + File.separator + "source").getPath();
  }

  public static File getApplicationBaseDir() {
    // Try the bundle path (deployed product)
    File dir = Platform.getProduct().getDefiningBundle().getDataFile("tmp").getParentFile();
    while ((dir = dir.getParentFile()) != null) {
      File possibleFlexSDKDir = new File(dir, "flex_sdk");
      if (possibleFlexSDKDir.exists()) {
        return possibleFlexSDKDir;
      }
    }
    
    // Try the product location (workbench)
    String installLocation = Platform.getInstallLocation().getURL().getPath();
    String productLocationURL = Platform.getProduct().getDefiningBundle().getLocation();
    
    String relativePath = productLocationURL.replace("initial@reference:file:", "");

    File productDir = new File(installLocation);
    int parentMarksCount = StringUtils.countMatches(relativePath, "..");
    for (int i = 0; i < parentMarksCount; i++) {
      productDir = productDir.getParentFile();
    }

    if (parentMarksCount > 0) {
      relativePath = relativePath.substring(StringUtils.lastIndexOf(relativePath, "..") + 3);
    }
    return new File(productDir, relativePath);
  }

}
