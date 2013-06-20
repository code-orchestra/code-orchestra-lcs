package codeOrchestra.lcs.license;

import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import codeOrchestra.lcs.license.plimus.PlimusHelper;
import codeOrchestra.lcs.license.plimus.PlimusResponse;
import codeOrchestra.lcs.license.plimus.PlimusResponseStatus;
import codeOrchestra.utils.DateUtils;

/**
 * @author Alexander Eliseyev
 */
public class PlimusSubscriptionExpirationStrategy extends AbstractExpirationWithSerialNumberStrategy implements ExpirationStrategy {

  
  private static final int EXPIRATION_DAYS = 15;  
  private static final String EXPIRE_LOCALLY_MILLIS = "expireLocally";
  private static final String DATE_STRING = "plimusTrialDate";  

  private static Preferences preferences = Preferences.userNodeForPackage(CodeOrchestraLicenseManager.class);

  private boolean isInTrialMode() {
    return CodeOrchestraLicenseManager.noSerialNumberPresent();
  }

  protected boolean handleValidationResponse(PlimusResponse plimusResponse) {
    if (plimusResponse.getStatus() == PlimusResponseStatus.SUCCESS) {
      return false;
    }

    return true;
  }

  boolean checkIfExpiredLocally() {
    return getSubscriptionDaysLeft() < 1;
  }

  @Override
  public boolean isTrialOnly() {
    return false;
  }

  @Override
  public boolean allowTrial() {
    return true;
  }

  @Override
  public boolean exitIfExpired() {
    return true;
  }

  @Override
  public void handleExpiration() {
  }

  @Override
  public boolean showLicenseExpiredDialog() {
    String expireMessage = isInTrialMode() ? "Your COLT trial period has expired. Browse to www.codeorchestra.com to purchase a subscription."
        : "Your COLT subscription has expired. Browse to www.codeorchestra.com to update the subscription.";

    MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "COLT License", null,
        expireMessage, MessageDialog.INFORMATION, new String[] { "Exit", "Enter Serial Number" }, 1);
    int result = dialog.open();
    if (result == 1) {
      return showSerialNumberDialog();
    }
    
    return false;
  }

  @Override
  public void showLicenseExpirationInProgressDialog() {
    if (isInTrialMode()) {
      String expireMessage = String.format("You have %d days of %d evaluation period days left. You may continue evaluation or enter a serial number",
          getDaysLeft(),
          getExpirationPeriod());
      
      MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "Evaluation License", null, expireMessage, MessageDialog.INFORMATION, 
          new String[] { "Continue Evaluation", "Enter Serial Number" }, 0);
      int result = dialog.open();    
      if (result == 1) {
        showSerialNumberDialog();
      }      
    } else {
      if (getSubscriptionDaysLeft() < 4) {
        String expireMessage = String.format("You have %d days of paid subscription left.",
            getSubscriptionDaysLeft());
        
        MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "Evaluation License", null, expireMessage, MessageDialog.INFORMATION, new String[] { "OK"}, 0);
        dialog.open();                 
      }      
    }
  }

  @Override
  public boolean isSubscriptionBased() {
    return true;
  }

  @Override
  public boolean hasExpired() {
    if (isInTrialMode()) {
      return super.hasExpired();
    } else {
      try {
        PlimusResponse validationResponse = PlimusHelper.validateKey(CodeOrchestraLicenseManager.getSerialNumber());
        return handleValidationResponse(validationResponse);
      } catch (IOException e) {
        return checkIfExpiredLocally();
      }
    }
  }

  public int getTrialDaysInUse() {
    long curentTime = System.currentTimeMillis();
    String currentTimeStr = String.valueOf(curentTime);

    long firstUsageDate = Long.valueOf(preferences.get(DATE_STRING, currentTimeStr));
    if (curentTime == firstUsageDate) {
      try {
        preferences.put(DATE_STRING, currentTimeStr);
        preferences.sync();
      } catch (BackingStoreException e) {
        throw new RuntimeException("Can't sync license expiry data", e);
      }
    }

    return (int) ((curentTime - firstUsageDate) / DateUtils.MILLIS_PER_DAY) + 1;
  }

  @Override
  public int getExpirationPeriod() {
    return EXPIRATION_DAYS;
  }

  private int getSubscriptionDaysLeft() {
    long expirationDateMillis = preferences.getLong(EXPIRE_LOCALLY_MILLIS, System.currentTimeMillis());
    return (int) ((expirationDateMillis - System.currentTimeMillis()) / DateUtils.MILLIS_PER_DAY) + 1;
  }
  
  @Override
  protected void registerProduct(String serialNumber, PlimusResponse keyRegistrationResponse) {
    CodeOrchestraLicenseManager.registerProduct(serialNumber);

    preferences.putLong(
        EXPIRE_LOCALLY_MILLIS,
        System.currentTimeMillis() + (keyRegistrationResponse.getDaysTillExpiration() * DateUtils.MILLIS_PER_DAY));
    try {
      preferences.sync();
    } catch (BackingStoreException e) {
      throw new RuntimeException("Can't sync license data", e);
    }
  }

}
