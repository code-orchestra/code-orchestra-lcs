package codeOrchestra.lcs.license;

import java.util.Date;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Alexander Eliseyev
 */
public class FastCompilerBetaExpirationStrategy implements ExpirationStrategy {

  @SuppressWarnings("deprecation")
  @Override
  public boolean hasExpired() {
    return new Date().after(new Date(113, 5, 24));
  }

  @Override
  public boolean isTrialOnly() {
    return true;
  }

  @Override
  public boolean allowTrial() {
    return true;
  }

  @Override
  public boolean showTrialExpiredDialog() {
    String expireMessage = "This copy of COLT has expired.";

    MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "COLT 1.1 (beta)", null,
        expireMessage, MessageDialog.INFORMATION, new String[] { "OK" }, 0);
    dialog.open();

    return false;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void showTrialInProgressDialog() {
    String expireMessage = String.format("This copy of COLT will expire on %s", new Date(113, 5, 24).toLocaleString());

    MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "COLT 1.1 (beta)", null,
        expireMessage, MessageDialog.INFORMATION, new String[] { "OK" }, 0);
    dialog.open();
  }

  @Override
  public boolean exitIfExpired() {
    return true;
  }

  @Override
  public void handleExpiration() {
  }

  @Override
  public boolean isSubscriptionBased() {
    return false;
  }

  @Override
  public boolean showSerialNumberDialog() {
    throw new UnsupportedOperationException();
  }

}
