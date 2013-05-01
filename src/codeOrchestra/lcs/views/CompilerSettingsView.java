package codeOrchestra.lcs.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import codeOrchestra.lcs.flex.FlexSDKManager;
import codeOrchestra.lcs.flex.FlexSDKNotPresentException;
import codeOrchestra.lcs.flex.FlexSDKSettings;
import codeOrchestra.lcs.project.CompilerSettings;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.utils.StringUtils;

/**
 * @author Alexander Eliseyev
 */
public class CompilerSettingsView extends LiveCodingProjectPartView<CompilerSettings> {

  public static final String ID = "LCS.compilerView";

  // Editors

  private FileFieldEditor mainClassEditor;
  private FileFieldEditor customCompilerConfigEditor;
  private BooleanFieldEditor defaultSDKBooleanEditor;
  private BooleanFieldEditor customSDKBooleanEditor;
  private StringFieldEditor outputFileNameEditor;
  private DirectoryFieldEditor flexSDKPathEditor;
  private ComboFieldEditor targetPlayerEditor;
  private BooleanFieldEditor useFrameworkAsRSLEditor;
  private BooleanFieldEditor nonDefaultLocaleEditor;
  private BooleanFieldEditor compilerStrictEditor;  
  private StringFieldEditor localeOptionsEditor;
  private StringFieldEditor compilerOptionsEditor;

  // Containers

  private Composite customCompilerConfigEditorComposite;
  private Composite targetPlayerComposite;
  private Composite localeOptionsEditorComposite;

  // Validation controls

  private Label targetPlayerErrorLabel;

  @Override
  public List<String> validate() {
    List<String> errors = new ArrayList<String>();
    
    String mainClassPath = mainClassEditor.getStringValue();
    if (StringUtils.isEmpty(mainClassPath) || !new File(mainClassPath).exists()) {
      errors.add("Invalid main class path " + mainClassPath);
    }
    
    String flexSDKPath = flexSDKPathEditor.getStringValue();
    if (StringUtils.isEmpty(flexSDKPath) || !new File(flexSDKPath).exists()) {
      errors.add("Invalid Flex SDK path " + flexSDKPath);
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

    Composite pathsComposite = new Composite(parent, SWT.NONE);
    pathsComposite.setLayout(new GridLayout());
    pathsComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

    Composite flexPathComposite = new Composite(pathsComposite, SWT.NONE);
    flexPathComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    flexSDKPathEditor = new DirectoryFieldEditor("flexSDKPath", "Flex SDK Path:", flexPathComposite);
    flexSDKPathEditor.setPreferenceStore(getPreferenceStore());
    flexSDKPathEditor.load();
    if (StringUtils.isEmpty(flexSDKPathEditor.getStringValue())) {
      flexSDKPathEditor.setStringValue(FlexSDKSettings.getDefaultFlexSDKPath());
    }
    flexSDKPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent event) {
        createTargetPlayerEditor(false);
      }
    });

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
    mainClassCompositeGridData.horizontalIndent = 3;
    mainClassComposite.setLayoutData(mainClassCompositeGridData);
    mainClassEditor = new FileFieldEditor("mainClass", "Main class:", mainClassComposite);
    mainClassEditor.setFileExtensions(new String[] { "*.as", "*.mxml" });
    mainClassEditor.setPreferenceStore(getPreferenceStore());

    Composite outputFileComposite = new Composite(generalCompilerSettingsGroup, SWT.NONE);
    outputFileComposite.setLayout(new GridLayout());
    GridData outputFileCompositeGridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
//    outputFileCompositeGridData.horizontalIndent = 5;
    outputFileComposite.setLayoutData(outputFileCompositeGridData);

    outputFileNameEditor = new StringFieldEditor("outputFileName", " Output file name:", outputFileComposite);
    outputFileNameEditor.setPreferenceStore(getPreferenceStore());

    // Target player composite
    // This control has to be saved/restored manually, w/o preferences store
    targetPlayerComposite = new Composite(generalCompilerSettingsGroup, SWT.NONE);
    targetPlayerComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    targetPlayerComposite.setLayout(new GridLayout(2, false));

    Composite useFrameworkAsRSLEditorComposite = new Composite(generalCompilerSettingsGroup, SWT.NONE);
    GridData useFrameworkAsRSLEditorCompositeGridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
    useFrameworkAsRSLEditorCompositeGridData.horizontalIndent = 10;
    useFrameworkAsRSLEditorComposite.setLayoutData(useFrameworkAsRSLEditorCompositeGridData);
    useFrameworkAsRSLEditor = new BooleanFieldEditor("useFrameworkAsRSL", "Use Framework as Runtime Shared Library (RSL)", useFrameworkAsRSLEditorComposite);
    useFrameworkAsRSLEditor.setPreferenceStore(getPreferenceStore());
    useFrameworkAsRSLEditorComposite.setLayout(new GridLayout());

    Composite nonDefaultLocaleComposite = new Composite(generalCompilerSettingsGroup, SWT.NONE);
    GridData nonDefaultLocaleCompositeGridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
    nonDefaultLocaleCompositeGridData.horizontalIndent = 10;
    nonDefaultLocaleComposite.setLayoutData(nonDefaultLocaleCompositeGridData);
    nonDefaultLocaleEditor = new BooleanFieldEditor("nonDefaultLocale", "Non-default locale settings:", nonDefaultLocaleComposite);
    nonDefaultLocaleEditor.setPreferenceStore(getPreferenceStore());
    nonDefaultLocaleEditor.setPropertyChangeListener(new IPropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent event) {
        updateUI();
      }
    });
    localeOptionsEditorComposite = new Composite(nonDefaultLocaleComposite, SWT.NONE);
    localeOptionsEditorComposite.setLayout(new GridLayout());
    localeOptionsEditorComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    localeOptionsEditor = new StringFieldEditor("localeOptions", "", localeOptionsEditorComposite);
    localeOptionsEditor.setPreferenceStore(getPreferenceStore());
    nonDefaultLocaleComposite.setLayout(new GridLayout(nonDefaultLocaleEditor.getNumberOfControls() + 1, false));
    
    Composite strictModeComposite = new Composite(generalCompilerSettingsGroup, SWT.NONE);
    GridData strictModeCompositeGridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
    strictModeCompositeGridData.horizontalIndent = 10;
    strictModeComposite.setLayoutData(strictModeCompositeGridData);
    compilerStrictEditor = new BooleanFieldEditor("compilerStrict", "Strict mode", strictModeComposite);
    compilerStrictEditor.setPreferenceStore(getPreferenceStore());
    strictModeComposite.setLayout(new GridLayout());

    Composite compilerOptionsEditorComposite = new Composite(parent, SWT.NONE);
    compilerOptionsEditorComposite.setLayout(new GridLayout());
    compilerOptionsEditorComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    compilerOptionsEditor = new StringFieldEditor("compilerOptions", "Additional compiler options:", compilerOptionsEditorComposite);
    compilerOptionsEditor.setPreferenceStore(getPreferenceStore());

    reset();
    createTargetPlayerEditor(true);
    updateUI();
  }

  private void createTargetPlayerEditor(boolean firstTimeCreate) {
    if (targetPlayerComposite.getChildren().length > 0) {
      for (Control control : targetPlayerComposite.getChildren()) {
        control.dispose();
      }
    }

    String[][] entryNamesAndValues;
    boolean invalidFlexSDK = false;
    try {
      List<String> playerVersions = FlexSDKManager.getInstance().getAvailablePlayerVersions(new File(flexSDKPathEditor.getStringValue()));
      entryNamesAndValues = new String[playerVersions.size()][2];

      int i = 0;
      for (String playerVersion : playerVersions) {
        entryNamesAndValues[i][0] = playerVersion;
        entryNamesAndValues[i][1] = playerVersion;
        i++;
      }
    } catch (FlexSDKNotPresentException e) {
      invalidFlexSDK = true;
      entryNamesAndValues = new String[0][2];
    }

    Composite targetPlayerEditorSubComposite = new Composite(targetPlayerComposite, SWT.NONE);
    targetPlayerEditorSubComposite.setLayout(new GridLayout());
    targetPlayerEditor = new ComboFieldEditor("targetPlayerVersion", "Target player version:", entryNamesAndValues, targetPlayerEditorSubComposite);
    targetPlayerEditor.setPreferenceStore(getPreferenceStore());
    if (firstTimeCreate) {
      targetPlayerEditor.load();
    } else if (!invalidFlexSDK) {
      getPreferenceStore().setValue("targetPlayerVersion", entryNamesAndValues[0][0]);
      targetPlayerEditor.load();
    }

    if (invalidFlexSDK) {
      targetPlayerErrorLabel = new Label(targetPlayerComposite, SWT.WRAP);
      targetPlayerErrorLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
      targetPlayerErrorLabel.setText("Incorrect Flex SDK path specified");
    }

    targetPlayerComposite.layout(true);
  }

  private void reset() {
    mainClassEditor.load();
    customCompilerConfigEditor.load();
    defaultSDKBooleanEditor.load();
    customSDKBooleanEditor.load();
    customCompilerConfigEditor.load();

    outputFileNameEditor.load();
    if (StringUtils.isEmpty(outputFileNameEditor.getStringValue())) {
      outputFileNameEditor.setStringValue(LCSProject.getCurrentProject().getName() + ".swf");
    }

    /*
    outputPathEditor.load();
    if (StringUtils.isEmpty(outputPathEditor.getStringValue())) {
      outputPathEditor.setStringValue(LCSProject.getCurrentProject().getOutputDir().getAbsolutePath());
    }
    */

    useFrameworkAsRSLEditor.load();
    nonDefaultLocaleEditor.load();
    compilerStrictEditor.load();
    localeOptionsEditor.load();
    compilerOptionsEditor.load();
  }

  @Override
  public void savePart() {
    flexSDKPathEditor.store();
    mainClassEditor.store();
    customCompilerConfigEditor.store();
    defaultSDKBooleanEditor.store();
    customSDKBooleanEditor.store();
    customCompilerConfigEditor.store();
    outputFileNameEditor.store();
//    outputPathEditor.store();
    targetPlayerEditor.store();
    useFrameworkAsRSLEditor.store();
    nonDefaultLocaleEditor.store();
    compilerStrictEditor.store();
    localeOptionsEditor.store();
    compilerOptionsEditor.store();
  }

  private void updateUI() {
    customCompilerConfigEditor.setEnabled(customSDKBooleanEditor.getBooleanValue(), customCompilerConfigEditorComposite);
    localeOptionsEditor.setEnabled(nonDefaultLocaleEditor.getBooleanValue(), localeOptionsEditorComposite);
  }

  @Override
  public void setFocus() {
  }

}
