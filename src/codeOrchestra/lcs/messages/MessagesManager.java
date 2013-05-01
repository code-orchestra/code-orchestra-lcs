package codeOrchestra.lcs.messages;

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

import codeOrchestra.lcs.views.MessagesView;

public class MessagesManager {

  private static MessagesManager instance;

  public static void init(IPageLayout layout, IFolderLayout folder) {
    MessagesManager.instance = new MessagesManager(folder, layout);
  }

  public static MessagesManager getInstance() {
    if (instance == null) {
      throw new IllegalStateException("MessagesManager must be initialized first");
    }
    return instance;
  }

  private IFolderLayout folder;
  private transient String lastScopeName;
  private Set<String> compositeViewIds = new HashSet<String>();

  private Map<String, MessagesView> messageScopeViews = new HashMap<String, MessagesView>();
  private final IPageLayout layout;

  public MessagesManager(IFolderLayout folder, IPageLayout layout) {
    this.folder = folder;
    this.layout = layout;
  }

  public void addTab(final String name) {
    this.lastScopeName = name;

    final String compositeViewId = MessagesView.ID + ":" + name;

    if (!compositeViewIds.contains(compositeViewId)) {
      Display.getDefault().syncExec(new Runnable() {
        public void run() {
          IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
          try {
            workbenchWindows[0].getActivePage().showView(MessagesView.ID, name, IWorkbenchPage.VIEW_ACTIVATE);
            layout.getViewLayout(MessagesView.ID).setCloseable(false);
          } catch (Throwable e) {
            folder.addView(compositeViewId);
            layout.getViewLayout(compositeViewId).setCloseable(false);
          }          
        }        
      });
      
      compositeViewIds.add(compositeViewId);
    }
  }

  public String getLastScopeName() {
    return lastScopeName;
  }

  public void reportViewCreated(String scopeName, MessagesView view) {
    messageScopeViews.put(scopeName, view);
  }

  public MessagesView getViewByName(String name) {
    return messageScopeViews.get(name);
  }
  
  public void clear() {
    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {
        for (MessagesView messageView : messageScopeViews.values()) {
          messageView.clear();
        }        
      }
    });    
  }

}
