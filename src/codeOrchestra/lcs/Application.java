package codeOrchestra.lcs;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import codeOrchestra.actionScript.logging.transport.LoggerServerSocketThread;
import codeOrchestra.http.CodeOrchestraHttpServer;
import codeOrchestra.lcs.license.LicenseManager;

/**
 * @author Alexander Eliseyev
 */
public class Application implements IApplication {

  public static Application INSTANCE = new Application(); 
  public Display display;
  
  private LoggerServerSocketThread serverSocketThread = new LoggerServerSocketThread();

  public Application() {
    INSTANCE = this;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.
   * IApplicationContext)
   */
  public Object start(IApplicationContext context) {
    display = PlatformUI.createDisplay();
    try {

      
      Object licenseReturnCode = LicenseManager.getInstance().interceptStart();
      if (licenseReturnCode != null) {
        return licenseReturnCode;
      }
      
      /*
      if (ExpirationHelper.hasExpired()) {
        display.syncExec(new Runnable() {
          public void run() {
            MessageDialog.openError(display.getActiveShell(), "COLT 1.0 Beta", "Your copy of Code Orchestra Livecoding Tool has expired");
          }
        });
        return IApplication.EXIT_OK;
      }
      */

      display.addListener(SWT.OpenDocument, new OpenDocumentEventProcessor());
      
      // TODO: handle errors
      CodeOrchestraHttpServer.getInstance().init();
      getServerSocketThread().openSocket();

      int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor(this));
      if (returnCode == PlatformUI.RETURN_RESTART) {
        return IApplication.EXIT_RESTART;
      }
      return IApplication.EXIT_OK;
    } finally {
      display.dispose();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.equinox.app.IApplication#stop()
   */
  public void stop() {
    if (!PlatformUI.isWorkbenchRunning())
      return;

    CodeOrchestraHttpServer.getInstance().dispose();
    getServerSocketThread().closeSocket();

    final IWorkbench workbench = PlatformUI.getWorkbench();
    final Display display = workbench.getDisplay();
    display.syncExec(new Runnable() {
      public void run() {
        if (!display.isDisposed())
          workbench.close();
      }
    });
  }

  public LoggerServerSocketThread getServerSocketThread() {
    return serverSocketThread;
  }

  public void setServerSocketThread(LoggerServerSocketThread serverSocketThread) {
    this.serverSocketThread = serverSocketThread;
  }

public Display getDisplay() {
	return display;
}

public void setDisplay(Display display) {
	this.display = display;
}
  
  
}
