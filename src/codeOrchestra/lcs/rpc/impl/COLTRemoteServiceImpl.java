package codeOrchestra.lcs.rpc.impl;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import codeOrchestra.lcs.errorhandling.ErrorHandler;
import codeOrchestra.lcs.project.ProjectManager;
import codeOrchestra.lcs.rpc.COLTRemoteService;
import codeOrchestra.lcs.rpc.model.COLTRemoteProject;

/**
 * @author Alexander Eliseyev
 */
public class COLTRemoteServiceImpl implements COLTRemoteService {

  private static COLTRemoteService instance;
  
  public synchronized static COLTRemoteService getInstance() {
    if (instance == null) {
      throw new IllegalStateException("COLTRemoteService not initialized yet");
    }
    return instance;
  }
  
  public synchronized static void init(IWorkbenchWindow window) {
    if (instance != null) {
      throw new IllegalStateException("COLTRemoteService not initialized yet");
    }
    instance = new COLTRemoteServiceImpl(window); 
  }
  
  private IWorkbenchWindow window;
  
  public COLTRemoteServiceImpl(IWorkbenchWindow window) {
    this.window = window;
  }

  public static Display getDisplay() {
    Display display = Display.getCurrent();
    if (display == null) {
      display = Display.getDefault();
    }
    return display;
  }

  @Override
  public void createProject(COLTRemoteProject project) {
    // TODO Auto-generated method stub    
    System.out.println();
  }
  
  public void loadProject(final String path) {
    final PartInitException[] exception = new PartInitException[1];

    getDisplay().syncExec(new Runnable() {        
      @Override
      public void run() {
        try {
          ProjectManager.getInstance().openProject(path, window);
        } catch (PartInitException e) {
          exception[0] = e;
        }    
      }
    });      
    
    if (exception[0] != null) {
      ErrorHandler.handle(exception[0], "Error while opening project " + path + " remotely");
      // TODO: report to client      
    }    
  }
  
}
