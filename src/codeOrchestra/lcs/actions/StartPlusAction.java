package codeOrchestra.lcs.actions;

import org.eclipse.jface.action.Action;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessHandler;

import codeOrchestra.lcs.Activator;
import codeOrchestra.actionScript.liveCoding.LiveCodingSession;
import codeOrchestra.actionScript.liveCoding.listener.LiveCodingAdapter;
import codeOrchestra.lcs.ICommandIds;
import codeOrchestra.lcs.errorhandling.ErrorHandler;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.run.LiveLauncher;
import codeOrchestra.lcs.run.LoggingProcessListener;
import codeOrchestra.lcs.session.LiveCodingManager;

public class StartPlusAction extends Action {
  
  public StartPlusAction() {
    setId(ICommandIds.CMD_START_PLUS);
    setActionDefinitionId(ICommandIds.CMD_START_PLUS);
    setEnabled(false);
    
    LiveCodingManager.instance().addListener(new LiveCodingAdapter() {
      @Override
      public void onSessionStart(LiveCodingSession session) {
        setEnabled(true);
      }
      @Override
      public void onSessionEnd(LiveCodingSession session) {
        setEnabled(false);
      }
    });
  }
  
  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    setImageDescriptor(enabled ? Activator.getImageDescriptor("/icons/run_add_blue2.png") : Activator.getImageDescriptor("/icons/run_add_disable.png"));    
  }
  
  @Override
  public void run() {
    try {
      ProcessHandler processHandler = new LiveLauncher().launch(LCSProject.getCurrentProject());
      processHandler.addProcessListener(new LoggingProcessListener("Launch"));
      processHandler.startNotify();
    } catch (ExecutionException e) {
      ErrorHandler.handle(e, "Error while launching build artifact");
      e.printStackTrace();
      setEnabled(true);
    }
  }

}
