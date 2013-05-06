package codeOrchestra.lcs.license;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import codeOrchestra.lcs.Activator;

/**
 * @author Alexander Eliseyev
 */
public final class VersionHelper {

  private static final String VERSION_CODE_NAME = "COLT1.0";

  public static final boolean IS_RELEASE_VERSION = true;

  private VersionHelper() {
  }

  public static String getVersionCodeName() {
    if (IS_RELEASE_VERSION) {
      return VERSION_CODE_NAME;
    }
    
    Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);    
    return VERSION_CODE_NAME + "m" + bundle.getVersion().getMinor();
  }

}
