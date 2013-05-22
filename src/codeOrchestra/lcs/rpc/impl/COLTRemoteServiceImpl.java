package codeOrchestra.lcs.rpc.impl;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import codeOrchestra.lcs.config.view.LiveCodingProjectViews;
import codeOrchestra.lcs.errorhandling.ErrorHandler;
import codeOrchestra.lcs.project.LCSProject;
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
      throw new IllegalStateException("COLTRemoteService was already initialized before");
    }
    instance = new COLTRemoteServiceImpl(window);
  }

  private IWorkbenchWindow window;

  public COLTRemoteServiceImpl(IWorkbenchWindow window) {
    this.window = window;
  }

  @Override
  public void createProject(String securityToken, final COLTRemoteProject remoteProject) {
    execute(securityToken, new RemoteCommand() {
      
      @Override
      public String getName() {
        return "Create project under " + remoteProject.getPath();
      }
      
      @Override
      public void execute() throws COLTRemoteException {
        File projectFile = new File(remoteProject.getPath());
        if (projectFile.exists()) {
          projectFile.delete();
        }
        try {
          projectFile.createNewFile();
        } catch (IOException e) {
          throw new COLTRemoteException("Error while creating project file", e);
        }
        
        LiveCodingProjectViews.closeProjectViews();
        
        LCSProject newProject = LCSProject.createNew(remoteProject.getName(), remoteProject.getPath());
        remoteProject.copyTo(newProject);
        
        try {
          LiveCodingProjectViews.openProjectViews(window, newProject);
        } catch (PartInitException e) {
          throw new COLTRemoteException("Error while opening project file", e);
        }        
      }      
    });
  }

  public void loadProject(String securityToken, final String path) {
    execute(securityToken, new RemoteCommand() {
      
      @Override
      public String getName() {
        return "Load project " + path;
      }

      @Override
      public void execute() throws COLTRemoteException {
        try {
          ProjectManager.getInstance().openProject(path, window);
        } catch (PartInitException e) {
          throw new COLTRemoteException(e);
        }
      }
      
    });
  }
  
  private void execute(String securityToken, final RemoteCommand command) {
    final COLTRemoteException[] exception = new COLTRemoteException[1];

    getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          command.execute();
        } catch (COLTRemoteException t) {
          exception[0] = t;
        }
      }
    });

    if (exception[0] != null) {
      ErrorHandler.handle(exception[0], "Error while handling remote command: " + command.getName());
      // TODO: report to client
    }
  }
  
  public static Display getDisplay() {
    Display display = Display.getCurrent();
    if (display == null) {
      display = Display.getDefault();
    }
    return display;
  }

  private static interface RemoteCommand {   
    String getName();    
    void execute() throws COLTRemoteException;    
  }

}
