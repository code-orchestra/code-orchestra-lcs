package codeOrchestra.lcs.license;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import codeOrchestra.lcs.license.plimus.PlimusResponse;

/**
 * @author Alexander Eliseyev
 */
public abstract class AbstractExpirationWithSerialNumberStrategy extends AbstractExpirationStrategy implements ExpirationStrategy {

  @Override
  public void showLicenseExpirationInProgressDialog() {
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
   
  protected abstract void registerProduct(String serialNumber, PlimusResponse keyRegistrationResponse);
  
}
