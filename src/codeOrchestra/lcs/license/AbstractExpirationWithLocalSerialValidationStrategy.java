package codeOrchestra.lcs.license;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import codeOrchestra.lcs.macAddress.SystemCheck;

/**
 * @author Alexander Eliseyev
 */
public abstract class AbstractExpirationWithLocalSerialValidationStrategy extends AbstractExpirationWithSerialNumberStrategy {

  public boolean showSerialNumberDialog() {
    InputDialog inputDialog = new InputDialog(Display.getDefault().getActiveShell(), "Serial number", "Please type the serial number purchased", null, null);
    if (inputDialog.open() == Window.CANCEL) {
      return false;
    }
    
    String serialNumber = inputDialog.getValue();
    if (serialNumber != null) {
      if (SystemCheck.getInstance().isValidMACAddress(serialNumber)) {
        MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Serial number", "Thank you for choosing the Code Orchestra Livecoding Tool!");        
        registerProduct(serialNumber, null);
        return true;        
      } else {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Serial number", "The serial number entered is invalid.");
        return showSerialNumberDialog();        
      }
    }

    return false;
  }
  
}
