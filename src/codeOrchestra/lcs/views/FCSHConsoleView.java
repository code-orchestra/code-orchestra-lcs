package codeOrchestra.lcs.views;

import java.util.ArrayList;
import java.util.List;

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

/**
 * @author Alexander Eliseyev
 */
public class FCSHConsoleView extends ViewPart {

  private static final int MAX_LINES_NUMBER = 30;

  public static final String ID = "LCS.fcshConsole";
  
  private static FCSHConsoleView sharedInstance;
  
  public static FCSHConsoleView get() {
    return sharedInstance;
  }
 
  private Text consoleTextArea;
  
  private List<String> currentText = new ArrayList<String>();

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
    
    new Thread() {
      public void run() {
        while (true) {
          try {
            sleep(1500);
          } catch (InterruptedException e) {
          }
          
          Display.getDefault().asyncExec(new Runnable() {
            public void run() {
              if (consoleTextArea.isDisposed()) {
                return;
              }
              
              consoleTextArea.setText(getText());
            }
          });
        }
      };
    }.start();
  }
  
  private synchronized String getText() {
    StringBuilder sb = new StringBuilder();
    for (String str : currentText) {
      sb.append(str);
    }
    return sb.toString();
  }
 
  @Override
  public void setFocus() {
  }

  public synchronized void write(String s) {
    if (currentText.size() > MAX_LINES_NUMBER) {
      currentText.remove(0);
    }
    currentText.add(s);    
  }
  
}
