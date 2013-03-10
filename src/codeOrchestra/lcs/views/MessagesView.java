package codeOrchestra.lcs.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import codeOrchestra.lcs.logging.Level;
import codeOrchestra.lcs.messages.MessagesManager;

public class MessagesView extends ViewPart {

  public static final String ID = "LCS.messages";

  private Table table;
  private String scopeName;

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

    // Controls
    Composite buttonsComposite = new Composite(parent, SWT.NONE);
    buttonsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
    GridLayout buttonsLayout = new GridLayout(1, true);
    buttonsLayout.marginHeight = 0;
    buttonsLayout.marginWidth = 0;
    buttonsLayout.horizontalSpacing = 0;
    buttonsLayout.verticalSpacing = 0;
    buttonsComposite.setLayout(buttonsLayout);

    Button warningButton = new Button(buttonsComposite, SWT.TOGGLE | SWT.FLAT);
    warningButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
    warningButton.setImage(Level.WARN.getImage());

    Button infoButton = new Button(buttonsComposite, SWT.TOGGLE | SWT.FLAT);
    infoButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
    infoButton.setImage(Level.INFO.getImage());

    Button sourceButton = new Button(buttonsComposite, SWT.TOGGLE | SWT.FLAT);
    sourceButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
    sourceButton.setImage(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/messages/source.png").createImage());

    // Table
    table = new Table(parent, SWT.BORDER);
    GridData tableLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
    table.setLayoutData(tableLayoutData);
  }

  public void addMessage(final Level level, final String message) {
    Display.getDefault().asyncExec(new Runnable() {
      public void run() {
        TableItem item = new TableItem(table, SWT.NONE);
        item.setText(message);
        item.setImage(level.getImage());
      }
    });
  }

  @Override
  public void setFocus() {
  }

}
