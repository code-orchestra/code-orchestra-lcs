package codeOrchestra.lcs.status;

import org.eclipse.swt.widgets.Display;

import codeOrchestra.actionScript.liveCoding.LiveCodingSession;
import codeOrchestra.actionScript.liveCoding.listener.LiveCodingAdapter;
import codeOrchestra.lcs.session.LiveCodingManager;

/**
 * @author Alexander Eliseyev
 */
public class SessionStatusLineItem extends StatusLineContributionItemEx {
 
  public SessionStatusLineItem() {
    super("SessionStatus");
    
    LiveCodingManager.instance().addListener(new LiveCodingAdapter() {
      @Override
      public void onSessionStart(LiveCodingSession session) {
        updateStatus();
      }
      @Override
      public void onSessionEnd(LiveCodingSession session) {
        updateStatus();
      }
    });
    
    updateStatus();
  }

  private void updateStatus() {
    final LiveCodingSession currentSession = LiveCodingManager.instance().getCurrentSession();
    
    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {
        if (currentSession == null) {
          setText("No active livecoding session");
          setImage(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/live-coding-disable.png").createImage());
        } else {
          setText("Active livecoding session: #" + currentSession.getSessionId());
          setImage(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/live-coding-enable.png").createImage());      
        }    
      }
    });
  }
  
}
