package codeOrchestra.lcs.rpc.security.view;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Alexander Eliseyev
 */
public class ShortCodeDialog extends TitleAreaDialog {

  private String requestor;
  private String shortCode;

  public ShortCodeDialog(Shell parentShell, String requestor, String shortCode) {
    super(parentShell);
    this.requestor = requestor;
    this.shortCode = shortCode;
  }

  @Override
  public void create() {
    super.create();
    setTitle("External Tool Authorization");
    setMessage("'" + requestor + "' has requested authorization to use COLT API.\n To authorize, enter the following code into that tool:",
        IMessageProvider.INFORMATION);
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    GridLayout layout = new GridLayout();
    parent.setLayout(layout);

    // The text fields will grow with the size of the dialog
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = GridData.CENTER;
    gridData.verticalAlignment = GridData.CENTER;

    Label codeLabel = new Label(parent, SWT.NONE);
    codeLabel.setLayoutData(gridData);
    codeLabel.setText(shortCode);
    FontData[] fD = codeLabel.getFont().getFontData();
    fD[0].setHeight(32);
    codeLabel.setFont(new Font(parent.getDisplay(), fD[0]));

    return parent;
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    GridData gridData = new GridData();
    gridData.verticalAlignment = GridData.CENTER;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = SWT.CENTER;

    parent.setLayoutData(gridData);
    createOkButton(parent, OK, "OK", true);
  }

  protected Button createOkButton(Composite parent, int id, String label, boolean defaultButton) {
    // increment the number of columns in the button bar
    ((GridLayout) parent.getLayout()).numColumns++;
    Button button = new Button(parent, SWT.PUSH);
    button.setText(label);
    button.setFont(JFaceResources.getDialogFont());
    button.setData(new Integer(id));
    button.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        okPressed();
      }
    });
    if (defaultButton) {
      Shell shell = parent.getShell();
      if (shell != null) {
        shell.setDefaultButton(button);
      }
    }
    setButtonLayoutData(button);
    return button;
  }

  @Override
  protected boolean isResizable() {
    return false;
  }

}