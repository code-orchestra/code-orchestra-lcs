package codeOrchestra.lcs.status;

import java.util.Set;

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
    final Set<String> sessionClientIds = LiveCodingManager.instance().getCurrentSessionsCliensIds();
    
    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {
        if (sessionClientIds.isEmpty()) {
          setText("No active livecoding sessions");
          setImage(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/live-coding-disable.png").createImage());
        } else {
          setText("Active livecoding sessions: " + sessionClientIds.size());
          setImage(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/live-coding-enable.png").createImage());      
        }    
      }
    });
  }
  
}
