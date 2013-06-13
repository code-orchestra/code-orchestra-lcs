package codeOrchestra.lcs.license;

import java.io.IOException;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import codeOrchestra.lcs.license.plimus.PlimusExpirationStrategy;
import codeOrchestra.lcs.license.plimus.PlimusHelper;
import codeOrchestra.lcs.license.plimus.PlimusResponse;

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
    
    // Trial-only (beta versions) - no serial number is checked
    if (expirationStrategy.isTrialOnly()) {
      if (ExpirationHelper.hasExpired()) {
        expirationStrategy.showTrialExpiredDialog();
        return IApplication.EXIT_OK;        
      } else {
        if (UsagePeriods.getInstance().isCurrentTimePresentInUsagePeriods()) {
          MessageDialog.openError(Display.getDefault().getActiveShell(), "Evaluation License", "Something is wrong with the system clock\nCOLT was launched already on the currently set time.");        
          return IApplication.EXIT_OK;
        }      

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
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Evaluation License", "Something is wrong with the system clock\nCOLT was launched already on the currently set time.");        
        return IApplication.EXIT_OK;
      }      
      
      boolean expired = false;
      
      if (ExpirationHelper.hasExpired()) {
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
    
    // Subscription version with serial number entered - check if subscription is expired
    if (expirationStrategy.isSubscriptionBased() && !CodeOrchestraLicenseManager.noSerialNumberPresent() && expirationStrategy instanceof PlimusExpirationStrategy) {
      PlimusExpirationStrategy plimusExpirationStrategy = (PlimusExpirationStrategy) expirationStrategy;
      
      try {
        PlimusResponse validationResponse = PlimusHelper.validateKey(CodeOrchestraLicenseManager.getSerialNumber());
        if (!plimusExpirationStrategy.handleValidationResponse(validationResponse)) {
          return IApplication.EXIT_OK;
        }
      } catch (IOException e) {
        if (plimusExpirationStrategy.checkIfExpiredLocally() && !CodeOrchestraLicenseDialogs.showSerialNumberDialog()) {
          return IApplication.EXIT_OK;          
        }
      }
    }
    
    return null;
  }
  
}
