package codeOrchestra.lcs.license;

import org.eclipse.equinox.app.IApplication;

/**
 * @author Alexander Eliseyev
 */
public class LicenseManager {
  
  private final static LicenseManager instance = new LicenseManager();
  public static LicenseManager getInstance() {
    return instance;
  }

  public Object interceptStart() {
    if (isEvaluationLicense()) {
      final ExpirationStrategy expirationStrategy = ExpirationHelper.getExpirationStrategy();
      final boolean[] expired = new boolean[1];
      
      if (ExpirationHelper.hasExpired()) {
        expired[0] = !expirationStrategy.showTrialExpiredDialog();
      } else {
        expirationStrategy.showTrialInProgressDialog();
        expired[0] = false;
      }
      
      if (expired[0]) {
        expirationStrategy.handleExpiration();

        if (expirationStrategy.exitIfExpired()) {
          return IApplication.EXIT_OK;
        }
      }
    }
    
    return null;
  }
  
  public String getLicensedToMessage() {
    return CodeOrchestraLicenseManager.getLicensedTo();
  }
  
  public boolean isEvaluationLicense() {
    return !CodeOrchestraLicenseManager.isLicenseValid();
  }
  

}
