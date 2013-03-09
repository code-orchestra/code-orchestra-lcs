package codeOrchestra.lcs.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.part.ViewPart;

public class MessagesView extends ViewPart {

  public static final String ID = "LCS.messages";

  private List list;

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
    warningButton.setImage(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/messages/warning.png").createImage());

    Button infoButton = new Button(buttonsComposite, SWT.TOGGLE | SWT.FLAT);
    infoButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
    infoButton.setImage(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/messages/information.png").createImage());

    Button sourceButton = new Button(buttonsComposite, SWT.TOGGLE | SWT.FLAT);
    sourceButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
    sourceButton.setImage(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/messages/source.png").createImage());

    // Table
    list = new List(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
    GridData tableLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
    list.setLayoutData(tableLayoutData);    
  }

  public List getList() {
    return list;
  }

  @Override
  public void setFocus() {
  }

}
