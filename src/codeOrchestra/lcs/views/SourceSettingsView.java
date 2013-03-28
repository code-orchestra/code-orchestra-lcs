package codeOrchestra.lcs.views;

import org.eclipse.jface.preference.PathEditor;
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

  private PathEditor sourcePathsEditor;
  private PathEditor libraryPathsEditor;
  
  @Override
  public void savePart() {
    sourcePathsEditor.store();
    libraryPathsEditor.store();    
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
