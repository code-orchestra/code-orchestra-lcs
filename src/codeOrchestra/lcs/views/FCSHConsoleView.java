package codeOrchestra.lcs.views;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

public class FCSHConsoleView extends ViewPart {

  public static final String ID = "LCS.fcshConsole";
  
  private static FCSHConsoleView sharedInstance;
  
  public static FCSHConsoleView get() {
    return sharedInstance;
  }
 
  private Text consoleTextArea;
  
  @Override
  public void init(IViewSite site) throws PartInitException {
    super.init(site);    
    sharedInstance = this;
  }

  @Override
  public void createPartControl(Composite parent) {
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    parent.setLayout(layout);
    
    consoleTextArea = new Text(parent, SWT.MULTI | SWT.V_SCROLL);
    consoleTextArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    consoleTextArea.setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));
  }
  
  public void append(final String text, String key) {
    Display.getDefault().asyncExec(new Runnable() {
      public void run() {
        consoleTextArea.append(text.trim());        
        consoleTextArea.append("\n");        
        consoleTextArea.setSelection(consoleTextArea.getText().length());
      }
    });
  }

  @Override
  public void setFocus() {
  }
  
}
