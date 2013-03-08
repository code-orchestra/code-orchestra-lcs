package codeOrchestra.lcs.views;

import org.eclipse.jface.preference.PathEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import codeOrchestra.lcs.config.view.PathEditorEx;
import codeOrchestra.lcs.project.SourceSettings;

/**
 * @author Alexander Eliseyev
 */
public class SourceSettingsView extends LiveCodingProjectPartView<SourceSettings> {
  
  public static final String ID = "LCS.sourcesView";

  private PathEditor sourcePathsEditor;
  private PathEditor libraryPathsEditor;
  private StringFieldEditor mainClassEditor;
  
  @Override
  protected void savePart() {
    sourcePathsEditor.store();
    libraryPathsEditor.store();
    mainClassEditor.store();    
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

    // Controls

    Composite pathsComposite = new Composite(banner, SWT.NONE);
    pathsComposite.setLayout(new GridLayout());
    pathsComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

    Composite mainClassComposite = new Composite(pathsComposite, SWT.NONE);
    mainClassComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    mainClassEditor = new StringFieldEditor("mainClass", "Main class:", mainClassComposite);
    mainClassEditor.setPreferenceStore(getPreferenceStore());
    mainClassEditor.load();

    sourcePathsEditor = new PathEditorEx("sourcePaths", "Source Paths:", "Choose a Source Path", banner, true);
    sourcePathsEditor.setPreferenceStore(getPreferenceStore());
    sourcePathsEditor.load();

    libraryPathsEditor = new PathEditorEx("libraryPaths", "Library Paths:", "Choose a Library Path", banner, false);
    libraryPathsEditor.setPreferenceStore(getPreferenceStore());
    libraryPathsEditor.load();
  }

  @Override
  public void setFocus() {
    mainClassEditor.setFocus();    
  }

}
