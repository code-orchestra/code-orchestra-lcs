package codeOrchestra.lcs.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import codeOrchestra.lcs.config.view.LiveCodingProjectViews;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.project.LiveCodingSettings;
import codeOrchestra.lcs.run.index.IndexHTMLGenerator;
import codeOrchestra.lcs.session.LiveCodingManager;
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
  private Button generateIndexButton;
  
  private Button defaultTargetButton;
  private Button webAddressTargetButton;

  private Composite flashPlayerPathComposite;
  private Composite webAddressComposite;

  private IWorkbenchWindow window;
  
  @Override
  public void init(IViewSite site) throws PartInitException {    
    super.init(site);
    window = site.getWorkbenchWindow();
  }
  
  @Override
  public List<String> validate() {
    List<String> errors = new ArrayList<String>();
    
    // TODO: implement
    
    return errors;
  }
  
  @Override
  public void createPartControl(Composite parent) {
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    layout.marginHeight = 5;
    layout.marginWidth = 10;
    parent.setLayout(layout);

    Group targetSettingsGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
    targetSettingsGroup.setText("Target");
    GridLayout targetSettingsLayout = new GridLayout(3, false);
    targetSettingsLayout.marginHeight = 5;
    targetSettingsLayout.marginWidth = 5;
    targetSettingsGroup.setLayout(targetSettingsLayout);
    targetSettingsGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    
    // These two radio buttons use custom load/store mechanism
    defaultTargetButton = new Button(targetSettingsGroup, SWT.RADIO);
    defaultTargetButton.setText("Compiled SWF");
    GridData defaultTargetButtonLayoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
    defaultTargetButtonLayoutData.horizontalSpan = 3;
    defaultTargetButton.setLayoutData(defaultTargetButtonLayoutData);
    defaultTargetButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        updateUI();
      }      
    });    
    webAddressTargetButton = new Button(targetSettingsGroup, SWT.RADIO);
    webAddressTargetButton.setText("Web Address:");
    webAddressComposite = new Composite(targetSettingsGroup, SWT.NONE);
    webAddressComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    webAddressEditor = new StringFieldEditor("webAddress", "", webAddressComposite);
    webAddressEditor.setPreferenceStore(getPreferenceStore());
    generateIndexButton = new Button(targetSettingsGroup, SWT.PUSH);
    generateIndexButton.setText("Generate index.html");
    generateIndexButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        // 1 - save
        LCSProject currentProject = LCSProject.getCurrentProject();
        LiveCodingProjectViews.saveProjectViewsState(window, currentProject);   
        currentProject.save();
        
        // 2 - generate
        try {
          new IndexHTMLGenerator(currentProject).generate();
        } catch (IOException e1) {
          // TODO: handle nicely
          e1.printStackTrace();
        }
        
        // 3 - update address
        webAddressEditor.setStringValue(LiveCodingManager.instance().getWebOutputAddress() + "/index.html");
      }
    });
    
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
    startSessionPausedEditor.setEnabled(false, startSessionPausedComposite);
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
    generateIndexButton.setEnabled(webAddressTargetButton.getSelection());
    
    if (webAddressTargetButton.getSelection() && flashPlayerLauncherButton.getSelection()) {
      flashPlayerLauncherButton.setSelection(false);
      defaultLauncherButton.setSelection(true);
    }    
  }
  
  @Override
  public void setFocus() {
  }
  
}
