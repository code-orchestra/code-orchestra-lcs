package codeOrchestra.lcs.license;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import codeOrchestra.utils.StringUtils;

/**
 * @author Alexander Eliseyev
 */
public class LicenseManager {
  
  private final static LicenseManager instance = new LicenseManager();
  public static LicenseManager getInstance() {
    return instance;
  }

  public Object interceptStart() {
    // Report serial number every 10 seconds
    if (StringUtils.isNotEmpty(CodeOrchestraLicenseManager.getLegacySerialNumber())) {
      if (!new ActivationReporter(CodeOrchestraLicenseManager.getLegacySerialNumber()).report()) {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "COLT License", "COLT beta version requires an active internet connection to start.");        
        return IApplication.EXIT_OK;
      }
      
      new Thread() {
        public void run() {
          while (true) {            
            try {
              Thread.sleep(10000);
            } catch (InterruptedException e) {
              // ignore
            }
            new ActivationReporter(CodeOrchestraLicenseManager.getLegacySerialNumber()).report();
          }                    
        };
      }.start();      
    }
    
    final ExpirationStrategy expirationStrategy = ExpirationHelper.getExpirationStrategy();
    
    // Trial-only (beta versions) - no serial number is checked
    if (expirationStrategy.isTrialOnly()) {
      if (ExpirationHelper.getExpirationStrategy().hasExpired()) {
        expirationStrategy.showTrialExpiredDialog();
        return IApplication.EXIT_OK;        
      } else {
        if (UsagePeriods.getInstance().isCurrentTimePresentInUsagePeriods()) {
          MessageDialog.openError(Display.getDefault().getActiveShell(), "Evaluation License", "Something is wrong with the system clock\nCOLT was launched already on the currently set time.");        
          return IApplication.EXIT_OK;
        }      
        
        expirationStrategy.showTrialInProgressDialog();

        return null;
      }      
    }
    
    // No-trial version (serial-number only) 
    if (!expirationStrategy.allowTrial() && CodeOrchestraLicenseManager.noSerialNumberPresent()) {
      if (!expirationStrategy.showTrialExpiredDialog()) {
        return IApplication.EXIT_OK; 
      }
    }
    
    // Trial version with no serial
    if (expirationStrategy.allowTrial() && CodeOrchestraLicenseManager.noSerialNumberPresent()) {
      if (UsagePeriods.getInstance().isCurrentTimePresentInUsagePeriods()) {
        String title = expirationStrategy.isSubscriptionBased() ? "COLT Subscription" : "Evaluation License";
        MessageDialog.openError(Display.getDefault().getActiveShell(), title, "Something is wrong with the system clock\nCOLT was launched already on the currently set time.");        
        return IApplication.EXIT_OK;
      }      
      
      boolean expired = false;
      
      if (ExpirationHelper.getExpirationStrategy().hasExpired()) {
        expired = !expirationStrategy.showTrialExpiredDialog();
      } else {
        expirationStrategy.showTrialInProgressDialog();
        expired = false;
      }
      
      if (expired) {
        expirationStrategy.handleExpiration();

        if (expirationStrategy.exitIfExpired()) {
          return IApplication.EXIT_OK;
        }
      }
    }
    
    return null;
  }
  
}
