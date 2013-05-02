package codeOrchestra.lcs.session;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import codeOrchestra.actionScript.liveCoding.LiveCodingSession;
import codeOrchestra.actionScript.liveCoding.listener.LiveCodingAdapter;
import codeOrchestra.lcs.views.SessionView;

/**
 * @author Alexander Eliseyev
 */
public class SessionViewManager {

  private static SessionViewManager instance;
  
  public static void init(IPageLayout layout, IFolderLayout folder) {
    SessionViewManager.instance = new SessionViewManager(folder, layout);
  }
  public static SessionViewManager getInstance() {
    if (instance == null) {
      throw new IllegalStateException("Not initialized yet");
    }
    return instance;
  }
  
  private IFolderLayout folder;
  private IPageLayout layout;

  private String lastClientId;
  
  private Set<String> compositeViewIds = new HashSet<String>();
  
  private Map<String, SessionView> sessionViews = new HashMap<String, SessionView>();
  
  private SessionViewManager(IFolderLayout folder, IPageLayout layout) {
    this.folder = folder;
    this.layout = layout;
    
    LiveCodingManager.instance().addListener(new LiveCodingAdapter() {
      @Override
      public void onSessionStart(LiveCodingSession session) {
        addTab(session.getClientId());
      }
      @Override
      public void onSessionEnd(LiveCodingSession session) {
        SessionView sessionView = sessionViews.get(session.getClientId());
        closeTab(sessionView);
      }
    });
  }
  
  private void closeTab(final SessionView sessionView) {
    Display.getDefault().syncExec(new Runnable() {
      public void run() {
        IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
        try {
          workbenchWindows[0].getActivePage().hideView(sessionView);
        } catch (Throwable e) {
          // ignore
        }          
      }        
    });
  }
  
  public void addTab(final String clientId) {
    this.lastClientId = clientId;

    final String compositeViewId = SessionView.ID + ":" + clientId;

    if (!compositeViewIds.contains(compositeViewId)) {
      Display.getDefault().syncExec(new Runnable() {
        public void run() {
          IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
          try {
            workbenchWindows[0].getActivePage().showView(SessionView.ID, clientId, IWorkbenchPage.VIEW_ACTIVATE);
          } catch (Throwable e) {
            folder.addView(compositeViewId);
          }          
        }        
      });
      
      compositeViewIds.add(compositeViewId);
    }
  }
  
  public String getLastClientId() {
    return lastClientId;
  }
  
  public void reportViewCreated(String clientId, SessionView view) {
    sessionViews.put(clientId, view);
  }

  public SessionView getViewByName(String clientId) {
    return sessionViews.get(clientId);
  }  
  
}
