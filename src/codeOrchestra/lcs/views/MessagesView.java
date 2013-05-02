package codeOrchestra.lcs.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import codeOrchestra.lcs.logging.Level;
import codeOrchestra.lcs.messages.MessagesManager;

public class MessagesView extends ViewPart {

  public static final String ID = "LCS.messages";

  private String scopeName;

  private MessagesTable messagesTable;
  
  @Override
  public void init(IViewSite site) throws PartInitException {
    super.init(site);
    scopeName = MessagesManager.getInstance().getLastScopeName();
    setPartName(scopeName);

    MessagesManager.getInstance().reportViewCreated(scopeName, this);
  }

  @Override
  public void createPartControl(Composite parent) {
    GridLayout parentLayout = new GridLayout(2, false);
    parentLayout.marginHeight = 0;
    parentLayout.marginWidth = 0;
    parentLayout.horizontalSpacing = 0;
    parentLayout.verticalSpacing = 0;
    parent.setLayout(parentLayout);

    messagesTable = new MessagesTable(parent, SWT.NONE);
  }

  public void addMessage(final String source, final Level level, final String message, final long timestamp, final String stackTrace) {
    messagesTable.addMessage(source, level, message, timestamp, stackTrace);
  }

  public void clear() {
    messagesTable.clear();
  }

  @Override
  public void setFocus() {
  }
  
}
