package codeOrchestra.lcs.views;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import codeOrchestra.lcs.project.CompilerSettings;
import codeOrchestra.utils.DirectoryFieldEditorEx;
import codeOrchestra.utils.SWTUtil;

/**
 * @author Alexander Eliseyev
 */
public class CompilerSettingsView extends LiveCodingProjectPartView<CompilerSettings> {

  public static final String ID = "LCS.compilerView";
  
  // Controls
  
  private StringFieldEditor mainClassEditor;
  private FileFieldEditor customCompilerConfigEditor;
  private BooleanFieldEditor defaultSDKBooleanEditor;
  private BooleanFieldEditor customSDKBooleanEditor;
  private StringFieldEditor outputFileNameEditor;
  private DirectoryFieldEditor outputPathEditor;;
  
  // Containers
  
  private Composite customCompilerConfigEditorComposite;

  private Text playerVersion1;

  private Text playerVersion2;

  private Text playerVersion3;

    @Override
  public void createPartControl(Composite parent) {
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    layout.marginHeight = 5;
    layout.marginWidth = 10;
    parent.setLayout(layout);

    // Config file settings
    
    Composite configurationFileComposite = new Composite(parent, SWT.NONE);
    configurationFileComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    configurationFileComposite.setLayout(new GridLayout(2, false));
    
    Composite defaultSDKBooleanEditorComposite = new Composite(configurationFileComposite, SWT.NONE);
    defaultSDKBooleanEditor = new BooleanFieldEditor("useDefaultSDKConfiguration", "Use default SDK compiler configuration file", defaultSDKBooleanEditorComposite);
    defaultSDKBooleanEditor.setPreferenceStore(getPreferenceStore());
    GridData defaultSDKButtonLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
    defaultSDKButtonLayoutData.horizontalSpan = 2;
    defaultSDKBooleanEditorComposite.setLayoutData(defaultSDKButtonLayoutData);
    
    Composite customSDKBooleanEditorComposite = new Composite(configurationFileComposite, SWT.NONE);
    customSDKBooleanEditor = new BooleanFieldEditor("useCustomSDKConfiguration", "Use custom compiler configuration file", customSDKBooleanEditorComposite);
    customSDKBooleanEditor.setPreferenceStore(getPreferenceStore());
    customCompilerConfigEditorComposite = new Composite(configurationFileComposite, SWT.NONE);
    customCompilerConfigEditorComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    customCompilerConfigEditor = new FileFieldEditor("customConfigPath", "", customCompilerConfigEditorComposite);
    customCompilerConfigEditor.setPreferenceStore(getPreferenceStore());
    customSDKBooleanEditor.setPropertyChangeListener(new IPropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent event) {
        updateUI();
      }
    });
    
    // Unnamed group
    
    Group generalCompilerSettingsGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
    GridLayout generalCompilerSettingsLayout = new GridLayout();
    generalCompilerSettingsLayout.marginHeight = 10;
    generalCompilerSettingsLayout.marginWidth = 5;
    generalCompilerSettingsGroup.setLayout(generalCompilerSettingsLayout);
    generalCompilerSettingsGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    
    Composite mainClassComposite = new Composite(generalCompilerSettingsGroup, SWT.NONE);
    mainClassComposite.setLayout(new GridLayout());
    GridData mainClassCompositeGridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
    mainClassCompositeGridData.horizontalIndent = 10;
    mainClassComposite.setLayoutData(mainClassCompositeGridData);
    mainClassEditor = new StringFieldEditor("mainClass", "Main class:", mainClassComposite);
    mainClassEditor.setPreferenceStore(getPreferenceStore());
    
    // Compiler output group
    
    Group compilerOutputSettingsGroup = new Group(generalCompilerSettingsGroup, SWT.SHADOW_ETCHED_IN);
    compilerOutputSettingsGroup.setLayout(new GridLayout(3, false));
    GridData compilerOutputSettingsGroupLayoutData = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
    compilerOutputSettingsGroup.setLayoutData(compilerOutputSettingsGroupLayoutData);
    compilerOutputSettingsGroup.setText("Compiler Output");
    
    outputFileNameEditor = new StringFieldEditor("outputFileName", " Output file name:", compilerOutputSettingsGroup);
    SWTUtil.setEditorIndent(outputFileNameEditor, compilerOutputSettingsGroup, 5, 5);
    outputFileNameEditor.setPreferenceStore(getPreferenceStore());
    
    new Label(compilerOutputSettingsGroup, SWT.NONE); // cell filler
    
    outputPathEditor = new DirectoryFieldEditorEx("outputPath", " Output Path:", compilerOutputSettingsGroup);
    SWTUtil.setEditorIndent(outputPathEditor, compilerOutputSettingsGroup, 5, 5);
    outputPathEditor.setPreferenceStore(getPreferenceStore());    
    
    // Target player composite
    // This control has to be saved/restored manually, w/o preferences store
    Composite targetPlayerComposite = new Composite(generalCompilerSettingsGroup, SWT.NONE);
    targetPlayerComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    GridLayout targetPlayerCompositeLayout = new GridLayout(6, false);
    targetPlayerComposite.setLayout(targetPlayerCompositeLayout);
    Label targetPlayerLabel = new Label(targetPlayerComposite, SWT.NONE);
    targetPlayerLabel.setText("Target player version:");
    playerVersion1 = new Text(targetPlayerComposite, SWT.LEFT);
    Label dotLabel1 = new Label(targetPlayerComposite, SWT.NONE);
    dotLabel1.setText(".");
    playerVersion2 = new Text(targetPlayerComposite, SWT.LEFT);
    Label dotLabel2 = new Label(targetPlayerComposite, SWT.NONE);
    dotLabel2.setText(".");
    playerVersion3 = new Text(targetPlayerComposite, SWT.LEFT);
    
    // 
    
    reset();
    updateUI();
  }

  private void reset() {
    mainClassEditor.load();
    customCompilerConfigEditor.load();
    defaultSDKBooleanEditor.load();
    customSDKBooleanEditor.load();
    customCompilerConfigEditor.load();
    outputFileNameEditor.load();
    outputPathEditor.load();
    
    // TODO: load target player
  }
  
  @Override
  public void savePart() {
    mainClassEditor.store();
    customCompilerConfigEditor.store();
    defaultSDKBooleanEditor.store();
    customSDKBooleanEditor.store();
    customCompilerConfigEditor.store();
    outputFileNameEditor.store();
    outputPathEditor.store();
    
    // TODO: store target player
  }
  
  private void updateUI() {
    customCompilerConfigEditor.setEnabled(customSDKBooleanEditor.getBooleanValue(), customCompilerConfigEditorComposite);
  }
  
  @Override
  public void setFocus() {
  }
  




}
