package codeOrchestra.lcs.views;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import codeOrchestra.lcs.project.LiveCodingSettings;
import codeOrchestra.utils.DirectoryFieldEditorEx;
import codeOrchestra.utils.StringUtils;

/**
 * @author Alexander Eliseyev
 */
public class LiveCodingSettingsView extends LiveCodingProjectPartView<LiveCodingSettings> {

  public static final String ID = "LCS.liveCodingView";

  private DirectoryFieldEditorEx flashPlayerPathEditor;
  private StringFieldEditor webAddressEditor;
  private RadioGroupFieldEditor liveMethodsGroupEditor;
  private BooleanFieldEditor startSessionPausedEditor;
  private BooleanFieldEditor makeGettersSettersLiveEditor;
  private StringFieldEditor maxLoopIterationsEditor;
  
  private Button defaultLauncherButton;
  private Button flashPlayerLauncherButton;
  
  private Button defaultTargetButton;
  private Button webAddressTargetButton;

  private Composite flashPlayerPathComposite;
  private Composite webAddressComposite;
  
  @Override
  public void createPartControl(Composite parent) {
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    layout.marginHeight = 5;
    layout.marginWidth = 10;
    parent.setLayout(layout);

    Group targetSettingsGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
    targetSettingsGroup.setText("Target");
    GridLayout targetSettingsLayout = new GridLayout(2, false);
    targetSettingsLayout.marginHeight = 5;
    targetSettingsLayout.marginWidth = 5;
    targetSettingsGroup.setLayout(targetSettingsLayout);
    targetSettingsGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    
    // These two radio buttons use custom load/store mechanism
    defaultTargetButton = new Button(targetSettingsGroup, SWT.RADIO);
    defaultTargetButton.setText("Compiled SWF");
    GridData defaultTargetButtonLayoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
    defaultTargetButtonLayoutData.horizontalSpan = 2;
    defaultTargetButton.setLayoutData(defaultTargetButtonLayoutData);
    defaultTargetButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        updateUI();
      }      
    });    
    webAddressTargetButton = new Button(targetSettingsGroup, SWT.RADIO);
    webAddressTargetButton.setText("Web Address:");
    webAddressComposite = new Composite(targetSettingsGroup, SWT.NONE);
    webAddressComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    webAddressEditor = new StringFieldEditor("webAddress", "", webAddressComposite);
    webAddressEditor.setPreferenceStore(getPreferenceStore());
    
    Group launcherSettingsGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
    launcherSettingsGroup.setText("Launcher");
    GridLayout launcherSettingsLayout = new GridLayout(2, false);
    launcherSettingsLayout.marginHeight = 5;
    launcherSettingsLayout.marginWidth = 5;
    launcherSettingsGroup.setLayout(launcherSettingsLayout);
    launcherSettingsGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

    // These two radio buttons use custom load/store mechanism
    defaultLauncherButton = new Button(launcherSettingsGroup, SWT.RADIO);
    defaultLauncherButton.setText("System default application");
    GridData defaultLauncherButtonLayoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
    defaultLauncherButtonLayoutData.horizontalSpan = 2;
    defaultLauncherButton.setLayoutData(defaultLauncherButtonLayoutData);
    defaultLauncherButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        updateUI();
      }      
    });    
    flashPlayerLauncherButton = new Button(launcherSettingsGroup, SWT.RADIO);
    flashPlayerLauncherButton.setText("Flash Player");
    flashPlayerPathComposite = new Composite(launcherSettingsGroup, SWT.NONE);
    flashPlayerPathComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    flashPlayerPathEditor = new DirectoryFieldEditorEx("flashPlayerPath", "", flashPlayerPathComposite);
    flashPlayerPathEditor.setPreferenceStore(getPreferenceStore());
    
    Group liveSettingsGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
    liveSettingsGroup.setText("Live Settings");
    GridLayout liveSettingsLayout = new GridLayout(2, false);
    liveSettingsLayout.marginHeight = 5;
    liveSettingsLayout.marginWidth = 5;
    liveSettingsGroup.setLayout(liveSettingsLayout);
    liveSettingsGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    
    Label liveMethodsLabel = new Label(liveSettingsGroup, SWT.NONE);
    liveMethodsLabel.setText("Live Methods:");
    liveMethodsLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
    Composite liveMethodsComposite = new Composite(liveSettingsGroup, SWT.NONE);
    String[][] liveMethodOptions = new String[][] {
        { "Annotated with [Live]" , "annotated" },
        { "All the methods", "all" }
    };
    liveMethodsGroupEditor = new RadioGroupFieldEditor("liveMethods", "", 1, liveMethodOptions, liveMethodsComposite);
    liveMethodsGroupEditor.setPreferenceStore(getPreferenceStore());
    
    Label startSessionPausedLabel = new Label(liveSettingsGroup, SWT.NONE);
    startSessionPausedLabel.setText("Start Session Paused:");
    startSessionPausedLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
    Composite startSessionPausedComposite = new Composite(liveSettingsGroup, SWT.NONE);
    GridData startSessionPausedCompositeGridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
    startSessionPausedCompositeGridData.horizontalIndent = 8;
    startSessionPausedComposite.setLayoutData(startSessionPausedCompositeGridData);    
    startSessionPausedEditor = new BooleanFieldEditor("startSessionPaused", "", startSessionPausedComposite);
    startSessionPausedEditor.setPreferenceStore(getPreferenceStore());
    
    Label makeGettersSettersLiveLabel = new Label(liveSettingsGroup, SWT.NONE);
    makeGettersSettersLiveLabel.setText("Make Getters/Setters Live:");
    makeGettersSettersLiveLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
    Composite makeGettersSettersLiveComposite = new Composite(liveSettingsGroup, SWT.NONE);
    GridData makeGettersSettersLiveCompositeGridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
    makeGettersSettersLiveCompositeGridData.horizontalIndent = 8;
    makeGettersSettersLiveComposite.setLayoutData(makeGettersSettersLiveCompositeGridData);    
    makeGettersSettersLiveEditor = new BooleanFieldEditor("makeGettersSettersLive", "", makeGettersSettersLiveComposite);
    makeGettersSettersLiveEditor.setPreferenceStore(getPreferenceStore());
    
    Label maxLoopIterationsLabel = new Label(liveSettingsGroup, SWT.NONE);
    maxLoopIterationsLabel.setText("Max Loop Iterations:");
    maxLoopIterationsLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));    
    Composite maxLoopIterationsComposite = new Composite(liveSettingsGroup, SWT.NONE);
    GridData maxLoopIterationsCompositeGridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
    maxLoopIterationsComposite.setLayoutData(maxLoopIterationsCompositeGridData);    
    maxLoopIterationsEditor = new StringFieldEditor("maxLoopIterations", "", 7, maxLoopIterationsComposite);
    maxLoopIterationsEditor.setPreferenceStore(getPreferenceStore());
    
    reset();
    updateUI();
  }

  private void reset() {
    boolean flashPlayerLauncher = getPreferenceStore().getBoolean("flashPlayerLauncher");
    if (flashPlayerLauncher) {
      flashPlayerLauncherButton.setSelection(true);
    } else {
      defaultLauncherButton.setSelection(true);
    }
    
    boolean webAddressTarget = getPreferenceStore().getBoolean("webAddressTarget");
    if (webAddressTarget) {
      webAddressTargetButton.setSelection(true);
    } else {
      defaultTargetButton.setSelection(true);
    }
    
    webAddressEditor.load();
    flashPlayerPathEditor.load();
    liveMethodsGroupEditor.load();
    startSessionPausedEditor.load();
    makeGettersSettersLiveEditor.load();
    maxLoopIterationsEditor.load();
    
    if (StringUtils.isEmpty(maxLoopIterationsEditor.getStringValue())) {
      maxLoopIterationsEditor.setStringValue("10000");
    }
  }

  @Override
  public void savePart() {
    getPreferenceStore().setValue("flashPlayerLauncher", flashPlayerLauncherButton.getSelection());
    getPreferenceStore().setValue("webAddressTarget", webAddressTargetButton.getSelection());
    
    webAddressEditor.store();
    flashPlayerPathEditor.store();
    liveMethodsGroupEditor.store();
    startSessionPausedEditor.store();
    makeGettersSettersLiveEditor.store();
    maxLoopIterationsEditor.store();
  }

  private void updateUI() {
    flashPlayerPathEditor.setEnabled(flashPlayerLauncherButton.getSelection() && !webAddressTargetButton.getSelection(), flashPlayerPathComposite);
    flashPlayerLauncherButton.setEnabled(!webAddressTargetButton.getSelection());

    webAddressEditor.setEnabled(webAddressTargetButton.getSelection(), webAddressComposite);
    
    if (webAddressTargetButton.getSelection() && flashPlayerLauncherButton.getSelection()) {
      flashPlayerLauncherButton.setSelection(false);
      defaultLauncherButton.setSelection(true);
    }    
  }
  
  @Override
  public void setFocus() {
  }
  
}
