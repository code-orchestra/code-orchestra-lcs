package codeOrchestra.lcs.rpc.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import codeOrchestra.actionScript.liveCoding.LiveCodingSession;
import codeOrchestra.actionScript.modulemaker.CompilationResult;
import codeOrchestra.lcs.config.view.LiveCodingProjectViews;
import codeOrchestra.lcs.controller.COLTController;
import codeOrchestra.lcs.controller.COLTControllerCallbackEx;
import codeOrchestra.lcs.errorhandling.ErrorHandler;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.project.ProjectManager;
import codeOrchestra.lcs.rpc.COLTRemoteException;
import codeOrchestra.lcs.rpc.COLTRemoteService;
import codeOrchestra.lcs.rpc.COLTRemoteTransferableException;
import codeOrchestra.lcs.rpc.COLTUnhandledException;
import codeOrchestra.lcs.rpc.model.COLTCompilationResult;
import codeOrchestra.lcs.rpc.model.COLTConnection;
import codeOrchestra.lcs.rpc.model.COLTRemoteProject;
import codeOrchestra.lcs.rpc.model.COLTState;
import codeOrchestra.lcs.rpc.security.COLTRemoteSecuriryManager;
import codeOrchestra.lcs.rpc.security.InvalidAuthTokenException;
import codeOrchestra.lcs.rpc.security.InvalidShortCodeException;
import codeOrchestra.lcs.rpc.security.TooManyFailedCodeTypeAttemptsException;
import codeOrchestra.lcs.session.LiveCodingManager;

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

  private final Object monitor = new Object();

  public COLTRemoteServiceImpl(IWorkbenchWindow window) {
    this.window = window;
  }

  @Override
  public int ping() {
    return 0;
  }
  
  @Override
  public COLTCompilationResult runProductionCompilation(String securityToken, final boolean run) throws COLTRemoteTransferableException {
    return executeSecurilyAsyncInUI(securityToken, new RemoteAsyncCommand<COLTCompilationResult>() {
      @Override
      public String getName() {
        return "Production Compile" + (run ? " and Run" : "");
      }

      @Override
      public void execute(final COLTControllerCallbackEx<COLTCompilationResult> callback) {
        COLTController.startProductionCompilation(window, new COLTControllerCallbackEx<CompilationResult>() {
          @Override
          public void onComplete(CompilationResult successResult) {
            callback.onComplete(new COLTCompilationResult(successResult));
          }

          @Override
          public void onError(Throwable t, CompilationResult errorResult) {
            callback.onError(t, errorResult != null ? new COLTCompilationResult(errorResult) : null);
          }
        }, run, false);
      }
    });
  }

  @Override
  public COLTCompilationResult runBaseCompilation(String securityToken) throws COLTRemoteTransferableException {
    return executeSecurilyAsyncInUI(securityToken, new RemoteAsyncCommand<COLTCompilationResult>() {
      @Override
      public String getName() {
        return "Base Compile and Run";
      }

      @Override
      public void execute(final COLTControllerCallbackEx<COLTCompilationResult> callback) {
        COLTController.startBaseCompilationAndRun(window, new COLTControllerCallbackEx<CompilationResult>() {
          @Override
          public void onComplete(CompilationResult successResult) {
            callback.onComplete(new COLTCompilationResult(successResult));
          }

          @Override
          public void onError(Throwable t, CompilationResult errorResult) {
            callback.onError(t, errorResult != null ? new COLTCompilationResult(errorResult) : null);
          }
        }, false);
      }
    });
  }

  @Override
  public COLTState getState(String securityToken) throws COLTRemoteTransferableException {
    return executeSecurily(securityToken, new RemoteCommand<COLTState>() {
      @Override
      public String getName() {
        return "Get COLT state";
      }

      @Override
      public COLTState execute() throws COLTRemoteException {
        COLTState state = new COLTState();

        List<COLTConnection> coltConnections = new ArrayList<COLTConnection>();
        for (LiveCodingSession session : LiveCodingManager.instance().getCurrentConnections()) {
          if (!session.isDisposed()) {
            coltConnections.add(new COLTConnection(session));
          }
        }
        state.setActiveConnections(coltConnections.toArray(new COLTConnection[coltConnections.size()]));

        return state;
      }
    });
  }

  @Override
  public void createProject(String securityToken, final COLTRemoteProject remoteProject)
      throws COLTRemoteTransferableException {
    executeSecurilyInUI(securityToken, new RemoteCommand<Void>() {
      @Override
      public String getName() {
        return "Create project under " + remoteProject.getPath();
      }

      @Override
      public Void execute() throws COLTRemoteException {
        File projectFile = new File(remoteProject.getPath());
        if (projectFile.exists()) {
          projectFile.delete();
        }
        try {
          projectFile.createNewFile();
        } catch (IOException e) {
          throw new COLTRemoteException("Error while creating project file", e);
        }

        LCSProject currentProject = LCSProject.getCurrentProject();
        if (currentProject != null) {
          currentProject.setDisposed();
        }
        LiveCodingProjectViews.closeProjectViews();

        LCSProject newProject = LCSProject.createNew(remoteProject.getName(), remoteProject.getPath());
        remoteProject.copyTo(newProject);

        try {
          LiveCodingProjectViews.openProjectViews(window, newProject);
          window.getShell().setActive();
          return null;
        } catch (PartInitException e) {
          throw new COLTRemoteException("Error while opening project file", e);
        }
      }
    });
  }

  public void loadProject(String securityToken, final String path) throws COLTRemoteTransferableException {
    executeSecurilyInUI(securityToken, new RemoteCommand<Void>() {
      @Override
      public String getName() {
        return "Load project " + path;
      }

      @Override
      public Void execute() throws COLTRemoteException {
        try {
          ProjectManager.getInstance().openProject(path, window);
          return null;
        } catch (PartInitException e) {
          throw new COLTRemoteException(e);
        }
      }
    });
  }

  @SuppressWarnings("unchecked")
  private <T> T executeInDisplayAndWait(final RemoteCommand<T> command) throws COLTRemoteTransferableException {
    final Throwable[] exception = new Throwable[1];
    final Object[] result = new Object[1];

    getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          result[0] = command.execute();
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

    return (T) result[0];
  }

  @SuppressWarnings("unchecked")
  private <T> T executeInDisplayAsyncAndWait(final RemoteAsyncCommand<T> command)
      throws COLTRemoteTransferableException {
    final Throwable[] exception = new Throwable[1];
    final Object[] result = new Object[1];

    getDisplay().asyncExec(new Runnable() {
      @Override
      public void run() {
        command.execute(new COLTControllerCallbackEx<T>() {
          @Override
          public void onComplete(T successResult) {
            result[0] = successResult;

            synchronized (monitor) {
              monitor.notify();
            }
          }

          @Override
          public void onError(Throwable t, T errorResult) {
            exception[0] = t;
            result[0] = errorResult;

            synchronized (monitor) {
              monitor.notify();
            }
          }
        });
      }
    });

    synchronized (monitor) {
      try {
        monitor.wait();
      } catch (InterruptedException e) {
        // ignore
      }
    }

    if (exception[0] != null) {
      exception[0].printStackTrace();

      if (exception[0] instanceof COLTRemoteTransferableException) {
        throw (COLTRemoteTransferableException) exception[0];
      }

      ErrorHandler.handle(exception[0], "Error while handling remote command: " + command.getName());
      throw new COLTUnhandledException(exception[0]);
    }

    return (T) result[0];
  }

  private <T> T executeSecurily(String securityToken, final RemoteCommand<T> command)
      throws COLTRemoteTransferableException {
    if (!COLTRemoteSecuriryManager.getInstance().isValidToken(securityToken)) {
      throw new InvalidAuthTokenException();
    }

    try {
      return command.execute();
    } catch (COLTRemoteException e) {
      e.printStackTrace();

      if (e instanceof COLTRemoteTransferableException) {
        throw (COLTRemoteTransferableException) e;
      }

      ErrorHandler.handle(e, "Error while handling remote command: " + command.getName());
      throw new COLTUnhandledException(e);
    }
  }

  private <T> T executeSecurilyInUI(String securityToken, final RemoteCommand<T> command)
      throws COLTRemoteTransferableException {
    if (!COLTRemoteSecuriryManager.getInstance().isValidToken(securityToken)) {
      throw new InvalidAuthTokenException();
    }

    return executeInDisplayAndWait(command);
  }

  private <T> T executeSecurilyAsyncInUI(String securityToken, final RemoteAsyncCommand<T> command)
      throws COLTRemoteTransferableException {
    if (!COLTRemoteSecuriryManager.getInstance().isValidToken(securityToken)) {
      throw new InvalidAuthTokenException();
    }

    return executeInDisplayAsyncAndWait(command);
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
    executeInDisplayAndWait(new RemoteCommand<Void>() {
      @Override
      public String getName() {
        return "Request short code";
      }

      @Override
      public Void execute() throws COLTRemoteException {
        COLTRemoteSecuriryManager.getInstance().createNewTokenAndGetShortCode(requestor);
        return null;
      }
    });
  }

  @Override
  public String obtainAuthToken(String shortCode) throws TooManyFailedCodeTypeAttemptsException,
      InvalidShortCodeException {
    return COLTRemoteSecuriryManager.getInstance().obtainAuthToken(shortCode);
  }

  private static interface RemoteCommand<T> {
    String getName();

    T execute() throws COLTRemoteException;
  }

  private static interface RemoteAsyncCommand<T> {
    String getName();

    void execute(COLTControllerCallbackEx<T> callback);
  }

}
