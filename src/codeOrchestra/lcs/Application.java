package codeOrchestra.lcs;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import codeOrchestra.actionScript.logging.transport.LoggerServerSocketThread;
import codeOrchestra.http.CodeOrchestraRPCHttpServer;
import codeOrchestra.http.CodeOrchestraResourcesHttpServer;
import codeOrchestra.lcs.license.COLTRunningKey;
import codeOrchestra.lcs.license.LicenseManager;

/**
 * @author Alexander Eliseyev
 */
public class Application implements IApplication {

  public static Application INSTANCE = new Application(); 
  
  private LoggerServerSocketThread serverSocketThread = new LoggerServerSocketThread();

  public static long timeStarted;
  
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
    final Display display = PlatformUI.createDisplay();
    try {
      timeStarted = System.currentTimeMillis();
      
      // COLT-287
      System.setProperty ("jsse.enableSNIExtension", "false");
      
      display.addListener(SWT.OpenDocument, new OpenDocumentEventProcessor());
      
      Object licenseReturnCode = LicenseManager.getInstance().interceptStart();
      if (licenseReturnCode != null) {
        return licenseReturnCode;
      }
      
      // TODO: handle errors
      COLTRunningKey.setRunning(true);
      CodeOrchestraResourcesHttpServer.getInstance().init();
      CodeOrchestraRPCHttpServer.getInstance().init();
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

    CodeOrchestraResourcesHttpServer.getInstance().dispose();
    CodeOrchestraRPCHttpServer.getInstance().dispose();
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
}
