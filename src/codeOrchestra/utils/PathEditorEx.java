package codeOrchestra.utils;

import org.eclipse.jface.preference.PathEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

/**
 * @author Alexander Eliseyev
 */
public class PathEditorEx extends PathEditor {
	
	private boolean dirsOnly;
	
	private String text;	

	public PathEditorEx(String name, String labelText, String text, Composite parent, boolean dirsOnly) {
		super(name, labelText, text, parent);
		this.dirsOnly = dirsOnly;
		this.text = text;
		
		getAddButton().setText("Add");
	}
	
	@Override
	protected String getNewInputObject() {
		 if (dirsOnly) {
			 return super.getNewInputObject();
		 } else {
			 FileDialog fileDialog = new FileDialog(getShell(), SWT.SHEET);
			 fileDialog.setText(text);
			 String file = fileDialog.open();
			 
		        if (file != null) {
		        	file = file.trim();
		            if (file.length() == 0) {
						return null;
					}
		        }
		        return file;
		 }
	}
	

}
