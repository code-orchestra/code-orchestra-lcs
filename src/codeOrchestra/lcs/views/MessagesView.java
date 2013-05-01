package codeOrchestra.lcs.views;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.mags.remas.view.dialogs.TextAreaMessageDialog;

import codeOrchestra.lcs.logging.Level;
import codeOrchestra.lcs.messages.MessagesManager;

public class MessagesView extends ViewPart {

  public static final String ID = "LCS.messages";

  private Table table;
  private String scopeName;

  private Button sourceButton;
  private Button infoButton;
  private Button warningButton;

  private Map<Message, TableItem> messages = new LinkedHashMap<Message, TableItem>();

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

    warningButton = new Button(buttonsComposite, SWT.TOGGLE | SWT.FLAT);
    warningButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
    warningButton.setSelection(true);
    warningButton.setImage(Level.WARN.getImage());
    warningButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        refresh();
      }
    });

    infoButton = new Button(buttonsComposite, SWT.TOGGLE | SWT.FLAT);
    infoButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
    infoButton.setSelection(true);
    infoButton.setImage(Level.INFO.getImage());
    infoButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        refresh();
      }
    });

    sourceButton = new Button(buttonsComposite, SWT.TOGGLE | SWT.FLAT);
    sourceButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
    sourceButton.setImage(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/messages/source.png").createImage());
    sourceButton.setSelection(true);
    sourceButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        for (TableItem tableItem : table.getItems()) {
          for (Entry<Message, TableItem> entry : messages.entrySet()) {
            if (entry.getValue() == tableItem) {
              Message message = entry.getKey();
              tableItem.setText(message.getMessageText(sourceButton.getSelection()));
              break;
            }
          }
        }
      }
    });

    // Table
    table = new Table(parent, SWT.BORDER | SWT.MULTI);
    table.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (((e.stateMask & SWT.CTRL) != 0 || (e.stateMask & SWT.COMMAND) != 0) && e.keyCode == 99) {
          TableItem[] selection = table.getSelection();
          if (selection != null && selection.length > 0) {
            StringBuilder sb = new StringBuilder();

            for (TableItem selectedTableItem : selection) {
              sb.append(selectedTableItem.getText()).append("\n");
            }

            final Clipboard cb = new Clipboard(Display.getDefault());
            TextTransfer textTransfer = TextTransfer.getInstance();
            cb.setContents(new Object[] { sb.toString() }, new Transfer[] { textTransfer });
          }
        }
      }
    });
    GridData tableLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
    table.setLayoutData(tableLayoutData);

    final Menu tablePopup = new Menu(table);
    table.setMenu(tablePopup);

    table.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseDoubleClick(MouseEvent e) {
        TableItem[] selection = table.getSelection();
        if (selection != null && selection.length == 1) {
          for (Message message : messages.keySet()) {
            if (selection[0] == messages.get(message) && message.getStackTrace() != null) {
              TextAreaMessageDialog.openError(Display.getDefault().getActiveShell(), "Stack Trace", "Exception Stack Trace", message.getStackTrace());
            }
          }
        }
      }
    });

    final MenuItem moveBeforeItem = new MenuItem(tablePopup, SWT.NONE);
    moveBeforeItem.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(final SelectionEvent e) {
        clear();
      }
    });
    moveBeforeItem.setText("Clear");
  }

  private void refresh() {
    table.clearAll();
    table.setItemCount(0);

    List<Message> messagesList = new ArrayList<Message>(messages.keySet());

    for (Message message : messagesList) {
      if ((message.getLevel() == Level.INFO && !infoButton.getSelection()) || (message.getLevel() == Level.WARN && !warningButton.getSelection())) {
        continue;
      }

      TableItem tableItem = message.createTableItem(table, sourceButton.getSelection());
      messages.put(message, tableItem);
    }
  }

  public void addMessage(final String source, final Level level, final String message, final long timestamp, final String stackTrace) {
    Display.getDefault().asyncExec(new Runnable() {
      public void run() {
        // COLT-114
        if (sourceButton.isDisposed()) {
          return;
        }
        
        Message newMessage = new Message(source, level, message, timestamp, stackTrace);
        TableItem tableItem = newMessage.createTableItem(table, sourceButton.getSelection());
        if (stackTrace != null) {
          tableItem.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
        }
        messages.put(newMessage, tableItem);
        
        try {
          table.showItem(tableItem);
        } catch (Throwable t) {
          // ignore
        }
      }
    });
  }

  @Override
  public void setFocus() {
  }

  public void clear() {
    table.removeAll();
  }

}
