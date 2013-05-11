package codeOrchestra.lcs.views;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener4;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import codeOrchestra.lcs.logging.Level;
import codeOrchestra.lcs.messages.MessagesManager;

public class MessagesView extends ViewPart {

  public static final String ID = "LCS.messages";

  private String scopeName;

  private MessagesTable messagesTable;
  
  //http://stackoverflow.com/questions/10754814/adding-one-view-to-two-perspectives-which-is-visible-in-both-of-them-one-opened
  private final class PerspectiveListenerImplementation implements IPerspectiveListener4 {
      @Override
      public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
    	scopeName = MessagesManager.getInstance().getLastScopeName();
        setPartName(scopeName);  
        MessagesManager.getInstance().reportViewCreated(scopeName, MessagesView.this);
      }

      //@formatter:off
      @Override public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective, final String changeId) {}
      @Override public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective, final IWorkbenchPartReference partRef, final String changeId) {}
      @Override public void perspectiveSavedAs(final IWorkbenchPage page, final IPerspectiveDescriptor oldPerspective, final IPerspectiveDescriptor newPerspective) {}
      @Override public void perspectiveOpened(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {}
      @Override public void perspectiveDeactivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {}
      @Override public void perspectiveClosed(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {}
      @Override public void perspectivePreDeactivate(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {}
      //@formatter:on
  }
  private final IPerspectiveListener4 perspectiveListener = new PerspectiveListenerImplementation();

  @Override
  public void init(IViewSite site) throws PartInitException {
    super.init(site);
	site.getWorkbenchWindow().addPerspectiveListener(perspectiveListener);
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
