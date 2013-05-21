package codeOrchestra.lcs.license;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

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
      if (UsagePeriods.getInstance().isCurrentTimePresentInUsagePeriods()) {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Evaluation License", "Something is wrong with the system clock\nCOLT was launched already on the currently set time.");        
        return IApplication.EXIT_OK;
      }
      
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
