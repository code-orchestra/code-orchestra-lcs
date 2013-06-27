package codeOrchestra.lcs.license;

import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import codeOrchestra.lcs.errorhandling.ErrorHandler;
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
  private static final String LAST_VALIDATION_DATE_STRING = "lastValidationDate";

  private static Preferences preferences = Preferences.userNodeForPackage(CodeOrchestraLicenseManager.class);

  private boolean isInTrialMode() {
    return CodeOrchestraLicenseManager.noSerialNumberPresent();
  }

  protected boolean handleValidationResponse(PlimusResponse plimusResponse) {
    preferences.putLong(LAST_VALIDATION_DATE_STRING, System.currentTimeMillis());
    try {
      preferences.sync();
    } catch (BackingStoreException e) {
      throw new RuntimeException("Can't store key validation time");
    }
    
    if (plimusResponse.getStatus() == PlimusResponseStatus.SUCCESS) {
      return false;
    }

    return true;
  }

  public boolean showSerialNumberDialog() {
    InputDialog inputDialog = new InputDialog(Display.getDefault().getActiveShell(), "Serial number", "Please type the serial number purchased", null, null);
    if (inputDialog.open() == Window.CANCEL) {
      return false;
    }
    
    String serialNumber = inputDialog.getValue();
    if (serialNumber != null) {
      PlimusResponse keyRegistrationResponse;
      try {        
        keyRegistrationResponse = PlimusHelper.registerKey(serialNumber);
      } catch (IOException e) {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Serial number", "Can't reach the validation server. Make sure your internet connection is active.");
        ErrorHandler.handle(e);
        return showSerialNumberDialog();
      }      
      
      if (keyRegistrationResponse.getStatus() == PlimusResponseStatus.ERROR_INVALIDKEY) {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Serial number", "The serial number entered is invalid.");
        return showSerialNumberDialog();
      }

      if (keyRegistrationResponse.getStatus() == PlimusResponseStatus.ERROR_INVALIDPRODUCT) {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Serial number", "The serial number entered can't be validated.");
        return showSerialNumberDialog();
      }
      
      if (keyRegistrationResponse.getStatus() == PlimusResponseStatus.ERROR_EXPIREDKEY) {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Serial number", "The serial number entered had expired " + Math.abs(keyRegistrationResponse.getDaysTillExpiration()) + " days ago.");
        return showSerialNumberDialog();
      }

      if (keyRegistrationResponse.getStatus() == PlimusResponseStatus.ERROR_MAXCOUNT) {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Serial number", "The key entered has already been registered the maximum number of times.");
        return showSerialNumberDialog();
      }
      
      if (keyRegistrationResponse.getStatus() == PlimusResponseStatus.SUCCESS) {
        MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Serial number", "Thank you for choosing the Code Orchestra Livecoding Tool!");        
        registerProduct(serialNumber, keyRegistrationResponse);
        return true;
      } else {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Serial number", "The serial number entered can't be validated (" + keyRegistrationResponse.getStatus() + ").");
        return showSerialNumberDialog();
      }
    }

    return false;
  }
  
  private boolean checkIfExpiredLocally() {
    if (haventValidatedOnServerForTooLong()) {
      return true;
    }
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
  
  private boolean haventValidatedOnServerForTooLong() {
    long lastValidationTime = preferences.getLong(LAST_VALIDATION_DATE_STRING, 0);
    if (lastValidationTime == 0) {
      return false;
    }
    
    return (((System.currentTimeMillis() - lastValidationTime) / DateUtils.MILLIS_PER_DAY) + 1) > 6;
  }

  @Override
  public boolean showLicenseExpiredDialog() {
    if (haventValidatedOnServerForTooLong()) {
      String expireMessage = "Key validation requires an active internet connection";

      MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "COLT License", null,
          expireMessage, MessageDialog.INFORMATION, new String[] { "Exit" }, 0);
      dialog.open();
      
      return false;      
    }
    
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
        ErrorHandler.handle(e);
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
  
  @Override
  public boolean allowsDemo() {
    return false;
  }

  @Override
  public boolean isInDemoMode() {
    throw new UnsupportedOperationException();
  }

}
