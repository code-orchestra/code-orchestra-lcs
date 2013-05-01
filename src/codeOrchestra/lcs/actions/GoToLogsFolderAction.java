package codeOrchestra.lcs.actions;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;

import codeOrchestra.actionScript.run.BrowserUtil;
import codeOrchestra.lcs.ICommandIds;
import codeOrchestra.lcs.errorhandling.ErrorHandler;

public class GoToLogsFolderAction extends Action {

  public GoToLogsFolderAction() {
    setText("Browse to Logs Folder");
    setId(ICommandIds.CMD_GO_TO_LOGS_FOLDER);
    setActionDefinitionId(ICommandIds.CMD_GO_TO_LOGS_FOLDER);
  }
  
  public void run() {
    String logsDir = new File(Platform.getLogFileLocation().toString()).getParent();
    
    System.out.println(logsDir);
    ProcessBuilder launchBrowser = BrowserUtil.launchBrowser(logsDir);
    try {
      launchBrowser.start();
    } catch (IOException e) {
      ErrorHandler.handle(e, "Error while opening logs folder");
    }
  }

}
