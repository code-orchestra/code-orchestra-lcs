package codeOrchestra.lcs.rpc.security.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;

import codeOrchestra.lcs.rpc.security.COLTRemoteSecuriryManager;
import codeOrchestra.lcs.rpc.security.COLTRemoteSecurityListener;
import codeOrchestra.lcs.status.StatusLineContributionItemEx;

/**
 * @author Alexander Eliseyev
 */
public class SecurityTokenStatusLineItem extends StatusLineContributionItemEx {
  
  public static SecurityTokenStatusLineItem INSTANCE;
  
  private ToolTip toolTip;
  
  private String lastRequestor;
  private String lastCode;

  private ShortCodeDialog shortCodeDialog;

  public SecurityTokenStatusLineItem() {
    super("SecurityTokenStatusLineItem", 4);    
    
    setText("");
    setMouseListener(new MouseAdapter() {
      @Override
      public void mouseDown(MouseEvent e) {
        if (isIconVisible()) {
          showCodeMessageDialog();
        }
      }
    });
    
    COLTRemoteSecuriryManager.getInstance().addListener(new COLTRemoteSecurityListener() {
      @Override
      public void onNewRequest(String requestor, String shortCode) {
        SecurityTokenStatusLineItem.this.lastRequestor = requestor;
        SecurityTokenStatusLineItem.this.lastCode = shortCode;
        display();        
      }

      @Override
      public void onSuccessfulActivation(String shortCode) {
        if (shortCodeDialog != null && shortCode.equals(lastCode)) {
          toolTip.getDisplay().asyncExec(new Runnable() {
            public void run() {
              shortCodeDialog.close();    
            }
          });          
        }        
      }
    });

    INSTANCE = this;    
    setVisible(true);
  }
  
  private boolean isIconVisible() {
    return getImage() != null;
  }
  
  private void showCodeMessageDialog() {
    shortCodeDialog = new ShortCodeDialog(toolTip.getParent(), lastRequestor, lastCode);
    shortCodeDialog.create();    
    shortCodeDialog.open();
    
    hide();
  }
  
  public void init(Shell shell) {
    toolTip = new ToolTip(shell, SWT.BALLOON);
  }
  
  public void hide() {
    setVisible(false);    
    
    shortCodeDialog = null;
    setImage(null);
  }
  
  private void display() {    
    setVisible(true);
    
    setImage(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/key-icon.png").createImage());    
    
    
    
    if (toolTip.getParent().isVisible()) {
      toolTip.getParent().forceActive();
      toolTip.getParent().setMinimized(false);
      
      String message = "Attempted connection from " + lastRequestor;
      toolTip.setText(message);
      getLabel().setToolTipText(message);
      
      Point displayLocation = getDisplayLocation();
      toolTip.setLocation(new Point(displayLocation.x + 8, displayLocation.y + 8));
      toolTip.setVisible(true);
    }
  }

}
