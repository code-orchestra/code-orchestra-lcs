package codeOrchestra.lcs.messages;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IFolderLayout;

import codeOrchestra.lcs.views.MessagesView;

public class MessagesManager {

  private static MessagesManager instance;
  
  public static void init(IFolderLayout folder) {
    instance = new MessagesManager(folder);
  }
  
  public static MessagesManager getInstance() {
    if (instance == null) {
      throw new IllegalStateException("MessagesManager must be initialized first");
    }
    return instance;
  }
  
  private IFolderLayout folder;
  private transient String lastScopeName;
  
  private Map<String, MessagesView> messageScopeViews = new HashMap<String, MessagesView>();
  
  public MessagesManager(IFolderLayout folder) {
    this.folder = folder;
  }
  
  public void addTab(String name) {
    this.lastScopeName = name;
    folder.addView(MessagesView.ID);
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
  
  
}
