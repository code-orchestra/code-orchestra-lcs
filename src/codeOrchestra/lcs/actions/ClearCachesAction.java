package codeOrchestra.lcs.actions;

import org.eclipse.jface.action.Action;

import codeOrchestra.actionScript.compiler.fcsh.FCSHException;
import codeOrchestra.actionScript.compiler.fcsh.FCSHManager;
import codeOrchestra.lcs.ICommandIds;

public class ClearCachesAction extends Action {

  public ClearCachesAction() {
    setText("Clear Caches");
    setId(ICommandIds.CMD_CLEAR_CACHES);
    setActionDefinitionId(ICommandIds.CMD_CLEAR_CACHES);
  }
  
  @Override
  public void run() {
    try {
      FCSHManager.instance().deleteLivecodingCaches();
    } catch (FCSHException e) {
      // TODO: Handle nicely
      e.printStackTrace();
    }
  }
  
}
