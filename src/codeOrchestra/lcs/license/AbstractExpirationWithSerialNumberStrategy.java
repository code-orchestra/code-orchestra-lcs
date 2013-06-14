package codeOrchestra.lcs.license;

import java.io.IOException;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import codeOrchestra.lcs.license.plimus.PlimusHelper;
import codeOrchestra.lcs.license.plimus.PlimusResponse;
import codeOrchestra.lcs.license.plimus.PlimusResponseStatus;

/**
 * @author Alexander Eliseyev
 */
public abstract class AbstractExpirationWithSerialNumberStrategy extends AbstractExpirationStrategy implements ExpirationStrategy {

  @Override
  public void showTrialInProgressDialog() {
    String expireMessage = String.format("You have %d days of %d evaluation period days left. You may continue evaluation or enter a serial number",
      getDaysLeft(),
      getExpirationPeriod());
    
    MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "Evaluation License", null, expireMessage, MessageDialog.INFORMATION, 
        new String[] { "Continue Evaluation", "Enter Serial Number" }, 0);
    int result = dialog.open();    
    if (result == 1) {
      showSerialNumberDialog();
    }
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
        CodeOrchestraLicenseManager.registerProduct(serialNumber);
        return true;
      } else {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Serial number", "The serial number entered can't be validated (" + keyRegistrationResponse.getStatus() + ").");
        return showSerialNumberDialog();
      }
    }

    return false;
  }
  
}
