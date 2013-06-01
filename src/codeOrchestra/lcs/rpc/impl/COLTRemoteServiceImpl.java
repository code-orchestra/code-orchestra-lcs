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
import codeOrchestra.lcs.rpc.COLTRemoteException;
import codeOrchestra.lcs.rpc.COLTRemoteService;
import codeOrchestra.lcs.rpc.COLTRemoteTransferableException;
import codeOrchestra.lcs.rpc.COLTUnhandledException;
import codeOrchestra.lcs.rpc.model.COLTCompilationResult;
import codeOrchestra.lcs.rpc.model.COLTRemoteProject;
import codeOrchestra.lcs.rpc.model.COLTState;
import codeOrchestra.lcs.rpc.security.COLTRemoteSecuriryManager;
import codeOrchestra.lcs.rpc.security.InvalidAuthTokenException;
import codeOrchestra.lcs.rpc.security.InvalidShortCodeException;
import codeOrchestra.lcs.rpc.security.TooManyFailedCodeTypeAttemptsException;

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
  public COLTCompilationResult runBaseCompilation() throws COLTRemoteTransferableException {
    COLTCompilationResult[] compilationResult = new COLTCompilationResult[1];
    
    // TODO: implement
    
    return compilationResult[0];
  }
  
  @Override
  public COLTState getState() throws COLTRemoteTransferableException {
    // TODO: implement
    return null;
  }
  
  @Override
  public void createProject(String securityToken, final COLTRemoteProject remoteProject)
      throws COLTRemoteTransferableException {
    executeSecurily(securityToken, new RemoteCommand() {
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

  public void loadProject(String securityToken, final String path) throws COLTRemoteTransferableException {
    executeSecurily(securityToken, new RemoteCommand() {
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

  private void execute(final RemoteCommand command) throws COLTRemoteTransferableException {
    final Throwable[] exception = new Throwable[1];
    getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          command.execute();
        } catch (Throwable t) {
          exception[0] = t;
        }
      }
    });

    if (exception[0] != null) {
      exception[0].printStackTrace();
      
      if (exception[0] instanceof COLTRemoteTransferableException) {
        throw (COLTRemoteTransferableException) exception[0];
      }

      ErrorHandler.handle(exception[0], "Error while handling remote command: " + command.getName());
      throw new COLTUnhandledException(exception[0]);
    }
  }

  private void executeSecurily(String securityToken, final RemoteCommand command)
      throws COLTRemoteTransferableException {
    if (!COLTRemoteSecuriryManager.getInstance().isValidToken(securityToken)) {
      throw new InvalidAuthTokenException();
    }

    execute(command);
  }

  public static Display getDisplay() {
    Display display = Display.getCurrent();
    if (display == null) {
      display = Display.getDefault();
    }
    return display;
  }

  @Override
  public void requestShortCode(final String requestor) throws COLTRemoteTransferableException {
    execute(new RemoteCommand() {
      @Override
      public String getName() {
        return "Request short code";
      }

      @Override
      public void execute() throws COLTRemoteException {
        COLTRemoteSecuriryManager.getInstance().createNewTokenAndGetShortCode(requestor);
      }
    });
  }

  @Override
  public String obtainAuthToken(String shortCode) throws TooManyFailedCodeTypeAttemptsException,
      InvalidShortCodeException {
    return COLTRemoteSecuriryManager.getInstance().obtainAuthToken(shortCode);
  }
  
  private static interface RemoteCommand {
    String getName();
    void execute() throws COLTRemoteException;
  }

}
