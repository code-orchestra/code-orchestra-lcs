package codeOrchestra.lcs;

import java.io.File;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import codeOrchestra.http.CodeOrchestraHttpServer;
import codeOrchestra.lcs.errorhandling.ErrorHandler;
import codeOrchestra.lcs.license.UsagePeriods;
import codeOrchestra.lcs.project.ProjectManager;
import codeOrchestra.lcs.project.RecentProjects;
import codeOrchestra.lcs.rpc.COLTRemoteServiceServlet;
import codeOrchestra.lcs.rpc.impl.COLTRemoteServiceImpl;
import codeOrchestra.lcs.views.FCSHConsoleView;

/**
 * @author Alexander Eliseyev
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

  public static String pathToOpenOnStartup;
  
  private Application application;

  public ApplicationWorkbenchAdvisor(Application application) {
    this.application = application;
  }

  public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
    return new ApplicationWorkbenchWindowAdvisor(configurer);
  }

  public String getInitialWindowPerspectiveId() {
    return Perspective.ID;
  }

  @Override
  public void initialize(IWorkbenchConfigurer configurer) {
    super.initialize(configurer);

    Throwable socketInitException = application.getServerSocketThread().getSocketInitException();
    if (socketInitException != null) {
      MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Can't init tracing socket: " + socketInitException);
    }
  }
  
  @Override
  public boolean preShutdown() {    
    UsagePeriods.getInstance().addUsagePeriod(Application.timeStarted, System.currentTimeMillis());
    return super.preShutdown();
  }

  @Override
  public void postStartup() {
    // Hide FCSH console
    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    IWorkbenchPartReference myView = page.findViewReference(FCSHConsoleView.ID);
    page.setPartState(myView, IWorkbenchPage.STATE_MINIMIZED);

    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    
    // Open project requested
    if (pathToOpenOnStartup != null) {      
      try {
        ProjectManager.getInstance().openProject(pathToOpenOnStartup, window);
      } catch (PartInitException e) {
        ErrorHandler.handle(e, "Error while opening COLT project: " + pathToOpenOnStartup);
      }      
    }
    
    // Open recent project
    List<String> recentProjectsPaths = RecentProjects.getRecentProjectsPaths();
    if (!recentProjectsPaths.isEmpty()) {
      String lastProjectPath = recentProjectsPaths.get(0);
      if (new File(lastProjectPath).exists()) {
        try {
          ProjectManager.getInstance().openProject(lastProjectPath, window);
        } catch (PartInitException e) {
          ErrorHandler.handle(e, "Error while opening COLT project: " + lastProjectPath);
        }
      }
    }
    
    // Init remote service
    COLTRemoteServiceImpl.init(window);
    CodeOrchestraHttpServer.getInstance().addServlet(new COLTRemoteServiceServlet(), "/rpc");
  }

}
