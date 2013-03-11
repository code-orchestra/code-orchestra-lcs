package codeOrchestra.lcs.actions;

import org.eclipse.jface.action.Action;

import codeOrchestra.lcs.ICommandIds;

public class StartSessionAction extends Action {

  public StartSessionAction() {
    setText("Start Live Coding Session");
    setId(ICommandIds.CMD_START_SESSION);
    setActionDefinitionId(ICommandIds.CMD_START_SESSION);
    setImageDescriptor(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/run.png"));
  }
  
  @Override
  public void run() {
    // TODO: implement
  }
  
}
