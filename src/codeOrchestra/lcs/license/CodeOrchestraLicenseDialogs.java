package codeOrchestra.lcs.license;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import codeOrchestra.lcs.serialNumber.validation.SerialValidator;

/**
 * @author Alexander Eliseyev
 */
public class CodeOrchestraLicenseDialogs {

  public static void showTrialInProgressDialog() {
    String expireMessage = String.format("You have %d days of %d evaluation period days left. You may continue evaluation or enter a serial number",
      ExpirationHelper.getDaysLeft(),
      ExpirationHelper.getExpirationPeriod());
    
    MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "Evaluation License", null, expireMessage, MessageDialog.INFORMATION, 
        new String[] { "Continue Evaluation", "Enter Serial Number" }, 0);
    int result = dialog.open();    
    if (result == 1) {
      showSerialNumberDialog();
    }
  }

  public static boolean showSerialNumberDialog() {
    InputDialog inputDialog = new InputDialog(Display.getDefault().getActiveShell(), "Serial number", "Please type the serial number purchased", null, null);
    if (inputDialog.open() == Window.CANCEL) {
      return false;
    }
    
    String serialNumber = inputDialog.getValue();
    if (serialNumber != null) {
      if (SerialValidator.getInstance().isValidSerialNumber(serialNumber)) {
        MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Serial number", "Thank you for choosing the Code Orchestra Livecoding Tool!");
        CodeOrchestraLicenseManager.registerProduct(serialNumber);
        return true;
      } else {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Serial number", "The serial number entered is not valid");
        return showSerialNumberDialog();
      }
    }

    return false;
  }

}
