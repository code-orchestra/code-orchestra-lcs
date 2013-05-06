package codeOrchestra.lcs.actions;

import org.eclipse.jface.action.Action;

import codeOrchestra.lcs.ICommandIds;
import codeOrchestra.lcs.license.CodeOrchestraLicenseDialogs;

/**
 * @author Alexander Eliseyev
 */
public class EnterSerialNumberAction extends Action {
  
  public EnterSerialNumberAction() {
    setText("Enter Serial Number");
    setId(ICommandIds.CMD_ENTER_SERIAL);
    setActionDefinitionId(ICommandIds.CMD_ENTER_SERIAL);
  }
  
  @Override
  public void run() {
    CodeOrchestraLicenseDialogs.showSerialNumberDialog();
  }

}
