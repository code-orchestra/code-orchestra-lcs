package codeOrchestra.utils;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Alexander Eliseyev
 */
public class SWTUtil {
  
  public static void setEditorIndent(FieldEditor fieldEditor, Composite parent, int horizontal, int vertical) {
    setIndent(horizontal, vertical, fieldEditor.getLabelControl(parent));
    
    if (fieldEditor instanceof StringFieldEditor) {
      StringFieldEditor stringFieldEditor = (StringFieldEditor) fieldEditor;
      setIndent(horizontal, vertical, stringFieldEditor.getTextControl(parent));
    }    
    
    if (fieldEditor instanceof DirectoryFieldEditorEx) {
      DirectoryFieldEditorEx directoryFieldEditor = (DirectoryFieldEditorEx) fieldEditor;
      setIndent(horizontal, vertical, directoryFieldEditor.getChangeControl_(parent));
    }
  }

  private static void setIndent(int horizontal, int vertical, Control control) {
    Object layoutData = control.getLayoutData();
    
    if (layoutData != null && !(layoutData instanceof GridData)) {
      return;
    }

    GridData gridLayoutData = null;
    if (layoutData != null && layoutData instanceof GridData) {
      gridLayoutData = (GridData) layoutData;
    } else {
      gridLayoutData = new GridData();
      control.setLayoutData(layoutData);
    }
        
    gridLayoutData.horizontalIndent = horizontal;    
    gridLayoutData.verticalIndent = vertical;
  }

}
