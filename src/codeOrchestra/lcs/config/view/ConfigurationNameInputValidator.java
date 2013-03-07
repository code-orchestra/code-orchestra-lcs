package codeOrchestra.lcs.config.view;

import org.eclipse.jface.dialogs.IInputValidator;

public class ConfigurationNameInputValidator implements IInputValidator {

	@Override
	public String isValid(String newText) {
		if (newText == null || newText.trim().length() == 0) {
			return "Configuration name can't be empty";
		}
		return null;
	}
	
	
	

}
