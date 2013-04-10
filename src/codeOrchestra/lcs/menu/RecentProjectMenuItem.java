package codeOrchestra.lcs.menu;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import codeOrchestra.lcs.project.ProjectManager;
import codeOrchestra.lcs.project.RecentProjects;

/**
 * @author Alexander Eliseyev
 */
public class RecentProjectMenuItem extends ContributionItem {

  public RecentProjectMenuItem() {
  }

  public RecentProjectMenuItem(String id) {
    super(id);
  }

  @Override
  public boolean isDynamic() {
    return true;
  }
  
  @Override
  public void fill(Menu menu, int index) {
    MenuItem rootItem = new MenuItem(menu, SWT.CASCADE, index);
    rootItem.setText("Open Recent");

    Menu subMenu = new Menu(menu);
    rootItem.setMenu(subMenu);

    for (final String recentPath : RecentProjects.getRecentProjectsPaths()) {
      MenuItem menuItem = new MenuItem(subMenu, SWT.NONE);
      menuItem.setText(recentPath);

      menuItem.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent event) {
          IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
          try {
            ProjectManager.getInstance().openProject(recentPath, window);
          } catch (PartInitException e) {
            MessageDialog.openError(window.getShell(), "Error", "Error opening view:" + e.getMessage());
          }
        }
      });
    }
  }
}