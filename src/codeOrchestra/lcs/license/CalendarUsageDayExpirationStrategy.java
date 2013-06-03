package codeOrchestra.lcs.license;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import codeOrchestra.utils.DateUtils;

/**
 * @author Alexander Eliseyev
 */
public class CalendarUsageDayExpirationStrategy implements ExpirationStrategy {

  private static final int EXPIRATION_DAYS = 15;
  private static final int EXPIRED_SESSION_MINUTES = 15;

  private static final String DATE_STRING = VersionHelper.getVersionCodeName();

  private static final Preferences preferences = Preferences.userNodeForPackage(CalendarUsageDayExpirationStrategy.class);

  private long startUpTime;
  private Thread expirationThread;

  @Override
  public boolean exitIfExpired() {
    return true;
  }

  @Override
  public void handleExpiration() {
    this.startUpTime = System.currentTimeMillis();
    this.expirationThread = new Thread() {
      public void run() {
        while (true) {
          try {
            Thread.sleep(DateUtils.MILLIS_PER_MINUTE);
          } catch (InterruptedException e) {
            terminate();
            return;
          }

          if (System.currentTimeMillis() - startUpTime > DateUtils.MILLIS_PER_MINUTE * EXPIRED_SESSION_MINUTES) {
            terminate();
            return;
          }
        }
      }

      void terminate() {
        // RE-2715
        // Serial number must've been entered after the app start
        if (CodeOrchestraLicenseManager.isLicenseValid()) {
          return;
        }

        JOptionPane.showMessageDialog(
          null,
          "Expired license editor " + EXPIRED_SESSION_MINUTES + " minute(s) session is over, the program will now quit",
          "Expired license",
          JOptionPane.INFORMATION_MESSAGE);        

        PlatformUI.getWorkbench().close();
      }
    };
    this.expirationThread.start();
  }

  @Override
  public boolean showTrialExpiredDialog() {
    String expireMessage = String.format("%d day(s) evaluation license has expired. The programm will quit unless you enter a serial number", getExpirationPeriod());

    MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "Evaluation License", null, expireMessage, MessageDialog.QUESTION, 
        new String[] { "Exit", "Enter Serial Number" }, 0);
    
    int result = dialog.open();
    if (result == 1) {
      return CodeOrchestraLicenseDialogs.showSerialNumberDialog();
    }

    return false;
  }

  @Override
  public void showTrialInProgressDialog() {
    CodeOrchestraLicenseDialogs.showTrialInProgressDialog();
  }

  @Override
  public int getDaysInUse() {
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

}
