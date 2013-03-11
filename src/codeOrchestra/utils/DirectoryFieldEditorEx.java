package codeOrchestra.utils;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class DirectoryFieldEditorEx extends DirectoryFieldEditor {

  public DirectoryFieldEditorEx() {
    super();
  }

  public DirectoryFieldEditorEx(String name, String labelText, Composite parent) {
    super(name, labelText, parent);
  }

  public Button getChangeControl_(Composite parent) {
    return getChangeControl(parent);
  }
  
  
}
