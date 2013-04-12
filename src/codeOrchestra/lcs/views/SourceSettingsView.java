package codeOrchestra.lcs.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import codeOrchestra.lcs.project.SourceSettings;
import codeOrchestra.utils.PathEditorEx;

/**
 * @author Alexander Eliseyev
 */
public class SourceSettingsView extends LiveCodingProjectPartView<SourceSettings> {
  
  public static final String ID = "LCS.sourcesView";

  private PathEditorEx sourcePathsEditor;
  private PathEditorEx libraryPathsEditor;
  
  @Override
  public void savePart() {
    sourcePathsEditor.store();
    libraryPathsEditor.store();    
  }
  
  @Override
  public List<String> validate() {
    List<String> errors = new ArrayList<String>();
    
    List<String> sourcePaths = sourcePathsEditor.getItems();
    if (sourcePaths.isEmpty()) {
      errors.add("No source paths specified");
    }
    
    for (String sourcePath : sourcePaths) {
      if (!new File(sourcePath).exists()) {
        errors.add("Invalid source path " + sourcePath);
      }
    }
    
    return errors;
  }

  @Override
  public void createPartControl(Composite parent) {
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    layout.marginHeight = 5;
    layout.marginWidth = 10;
    parent.setLayout(layout);

    Composite banner = new Composite(parent, SWT.NONE);
    GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
    banner.setLayoutData(layoutData);

    sourcePathsEditor = new PathEditorEx("sourcePaths", "Source Paths:", "Choose a Source Path", banner, true);
    sourcePathsEditor.setPreferenceStore(getPreferenceStore());
    sourcePathsEditor.load();

    libraryPathsEditor = new PathEditorEx("libraryPaths", "Library Paths:", "Choose a Library Path", banner, false);
    libraryPathsEditor.setPreferenceStore(getPreferenceStore());
    libraryPathsEditor.load();   
  }

  @Override
  public void setFocus() {    
  }

}
