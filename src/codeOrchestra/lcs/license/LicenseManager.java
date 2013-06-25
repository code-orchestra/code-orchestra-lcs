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
    final ExpirationStrategy expirationStrategy = ExpirationHelper.getExpirationStrategy();
    
    // Report serial number every 10 seconds
    if (StringUtils.isNotEmpty(CodeOrchestraLicenseManager.getLegacySerialNumber())) {
      if (expirationStrategy.isTrialOnly() && !new ActivationReporter(CodeOrchestraLicenseManager.getLegacySerialNumber()).report()) {
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
    
    // Trial-only (beta versions) - no serial number is checked
    if (expirationStrategy.isTrialOnly()) {
      if (ExpirationHelper.getExpirationStrategy().hasExpired()) {
        expirationStrategy.showLicenseExpiredDialog();
        return IApplication.EXIT_OK;        
      } else {
        if (UsagePeriods.getInstance().isCurrentTimePresentInUsagePeriods()) {
          MessageDialog.openError(Display.getDefault().getActiveShell(), "Evaluation License", "Something is wrong with the system clock\nCOLT was launched already on the currently set time.");        
          return IApplication.EXIT_OK;
        }      
        
        expirationStrategy.showLicenseExpirationInProgressDialog();

        return null;
      }      
    }
    
    // No-trial version (serial-number only) 
    if (!expirationStrategy.allowTrial() && CodeOrchestraLicenseManager.noSerialNumberPresent() && !expirationStrategy.allowsDemo()) {
      if (!expirationStrategy.showLicenseExpiredDialog()) {
        return IApplication.EXIT_OK; 
      }
    }
    
    // Trial version with no serial
    if ((expirationStrategy.allowTrial() && CodeOrchestraLicenseManager.noSerialNumberPresent()) || (!expirationStrategy.allowsDemo() && expirationStrategy.isSubscriptionBased() && !CodeOrchestraLicenseManager.noSerialNumberPresent())) {
      if (UsagePeriods.getInstance().isCurrentTimePresentInUsagePeriods()) {
        String title = expirationStrategy.isSubscriptionBased() ? "COLT Subscription" : "Evaluation License";
        MessageDialog.openError(Display.getDefault().getActiveShell(), title, "Something is wrong with the system clock\nCOLT was launched already on the currently set time.");        
        return IApplication.EXIT_OK;
      }      
      
      boolean expired = false;
      
      if (ExpirationHelper.getExpirationStrategy().hasExpired()) {
        expired = !expirationStrategy.showLicenseExpiredDialog();
      } else {
        expirationStrategy.showLicenseExpirationInProgressDialog();
        expired = false;
      }
      
      if (expired) {
        expirationStrategy.handleExpiration();

        if (expirationStrategy.exitIfExpired()) {
          return IApplication.EXIT_OK;
        }
      }
    }
    
    // Demo version with subscription
    if (expirationStrategy.allowsDemo() && expirationStrategy.isSubscriptionBased()) {
      if (UsagePeriods.getInstance().isCurrentTimePresentInUsagePeriods()) {
        String title = "COLT Subscription";
        MessageDialog.openError(Display.getDefault().getActiveShell(), title, "Something is wrong with the system clock\nCOLT was launched already on the currently set time.");        
        return IApplication.EXIT_OK;
      }      
      
      boolean expired = false;      
      if (ExpirationHelper.getExpirationStrategy().hasExpired()) {
        expired = !expirationStrategy.showLicenseExpiredDialog();
      } else {
        expirationStrategy.showLicenseExpirationInProgressDialog();
        expired = false;
      }
      
      if (expired) {
        expirationStrategy.handleExpiration();
      }
    }
    
    return null;
  }
  
}
