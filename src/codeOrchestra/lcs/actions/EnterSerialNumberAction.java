package codeOrchestra.lcs.actions;

import org.eclipse.jface.action.Action;

import codeOrchestra.lcs.ICommandIds;
import codeOrchestra.lcs.config.view.LiveCodingProjectViews;
import codeOrchestra.lcs.license.CodeOrchestraLicenseManager;
import codeOrchestra.lcs.license.ExpirationHelper;

/**
 * @author Alexander Eliseyev
 */
public class EnterSerialNumberAction extends Action {
  
  public EnterSerialNumberAction() {
    setText("Enter Serial Number");
    setId(ICommandIds.CMD_ENTER_SERIAL);
    setActionDefinitionId(ICommandIds.CMD_ENTER_SERIAL);
    setEnabled(!ExpirationHelper.getExpirationStrategy().isTrialOnly() && CodeOrchestraLicenseManager.noSerialNumberPresent());
  }
  
  @Override
  public void run() {
    ExpirationHelper.getExpirationStrategy().showSerialNumberDialog();
    LiveCodingProjectViews.updateTitle();
  }

}
