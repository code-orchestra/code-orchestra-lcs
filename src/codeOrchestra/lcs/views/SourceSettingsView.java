package codeOrchestra.lcs.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.DirectoryFieldEditor;
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
  private PathEditorEx assetPathsEditor;
  private DirectoryFieldEditor htmlTemplatePathEditor;
  
  @Override
  public void savePart() {
    sourcePathsEditor.store();
    libraryPathsEditor.store();    
    assetPathsEditor.store();
    htmlTemplatePathEditor.store();
  }
  
  @Override
  public List<String> validate() {
    List<String> errors = new ArrayList<String>();
    
    // Sources
    List<String> sourcePaths = sourcePathsEditor.getItems();
    if (sourcePaths.isEmpty()) {
      errors.add("No source paths specified");
    }
    for (String sourcePath : sourcePaths) {
      if (!new File(sourcePath).exists()) {
        errors.add("Invalid source path " + sourcePath);
      }
    }
    
    // Libraries
    List<String> libraryPaths = libraryPathsEditor.getItems();
    for (String libraryPath : libraryPaths) {
      if (!new File(libraryPath).exists()) {
        errors.add("Invalid library path " + libraryPath);
      }
    }
    
    // Assets
    List<String> assetsPaths = assetPathsEditor.getItems();
    for (String assetPaths : assetsPaths) {
      if (!new File(assetPaths).exists()) {
        errors.add("Invalid source path " + assetPaths);
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
    banner.setLayout(new GridLayout(2, false));
    GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
    banner.setLayoutData(layoutData);

    sourcePathsEditor = new PathEditorEx("sourcePaths", "Source Paths:", "Choose a Source Path", banner, true);
    sourcePathsEditor.setPreferenceStore(getPreferenceStore());
    sourcePathsEditor.load();

    libraryPathsEditor = new PathEditorEx("libraryPaths", "Library Paths:", "Choose a Library Path", banner, false);
    libraryPathsEditor.setPreferenceStore(getPreferenceStore());
    libraryPathsEditor.load();   
    
    assetPathsEditor = new PathEditorEx("asssetPaths", "Assets Paths:", "Choose an Assets Folder Path", banner, true);
    assetPathsEditor.setPreferenceStore(getPreferenceStore());
    assetPathsEditor.load();
    
    Composite htmlTemplateComposite = new Composite(banner, SWT.NONE);
    GridData htmlTemplateCompositeLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
    htmlTemplateCompositeLayoutData.horizontalSpan = 2;
    htmlTemplateComposite.setLayoutData(htmlTemplateCompositeLayoutData);
    
    htmlTemplatePathEditor = new DirectoryFieldEditor("htmlTemplatePath", "HTML Template:", htmlTemplateComposite);
    htmlTemplatePathEditor.setPreferenceStore(getPreferenceStore());
    htmlTemplatePathEditor.load();
  }

  @Override
  public void setFocus() {    
  }

}
