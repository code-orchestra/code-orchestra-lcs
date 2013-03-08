package codeOrchestra.lcs.config.view;

import org.eclipse.jface.dialogs.IInputValidator;

public class NameInputValidator implements IInputValidator {

	private String entityName;
	
	public NameInputValidator(String entityName) {
		this.entityName = entityName;
	}

	@Override
	public String isValid(String newText) {
		if (newText == null || newText.trim().length() == 0) {
			return entityName +  " name can't be empty";
		}
		return null;
	}
	

}
