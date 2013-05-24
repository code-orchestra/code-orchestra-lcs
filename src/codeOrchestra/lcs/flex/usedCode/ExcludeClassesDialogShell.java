package codeOrchestra.lcs.flex.usedCode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import codeOrchestra.lcs.air.views.utils.ModalSettingsDialogShell;
import codeOrchestra.lcs.errorhandling.ErrorHandler;
import codeOrchestra.lcs.make.LCSMaker;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.project.ProjectPreferenceStore;
import codeOrchestra.utils.StringUtils;

/**
 * @author Alexander Eliseyev
 */
public class ExcludeClassesDialogShell extends ModalSettingsDialogShell {

  private IPreferenceStore preferenceStore;
  private Text excludedClassesTextArea;
  private Button generateButton;
  private Button okButton;
  private Button cancelButton;

  public ExcludeClassesDialogShell(IPreferenceStore preferenceStore) {
    super("Exclude Classes", 800, 600);
    this.preferenceStore = preferenceStore;

    createPartControl(getBaseDialogShell());
    loadPart();
  }
  
  public void toggleButtonsEnable(boolean enable) {
    generateButton.setEnabled(enable);
    okButton.setEnabled(enable);
    cancelButton.setEnabled(enable);
  }
  
  protected void generate() {
    new Job("Generating link report") {
      @Override
      protected IStatus run(IProgressMonitor monitor) {                
        try {
          Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
              toggleButtonsEnable(false);              
            }
          });          
          
          monitor.beginTask("Compilation", 100);

          LCSMaker lcsMaker = new LCSMaker(false);
          lcsMaker.setSkipSecondPhase(true);
          boolean makeOk = lcsMaker.make();          
          monitor.worked(80);
          
          if (!makeOk) {
            ErrorHandler.handle("Can't run compilation to build a link report, check the log");
            return Status.CANCEL_STATUS;            
          }
          
          LCSProject currentProject = LCSProject.getCurrentProject();
          File linkReportFile = currentProject.getLinkReportFile();
          if (!linkReportFile.exists()) {
            ErrorHandler.handle("Can't access a link report file, check the log");
            return Status.CANCEL_STATUS;
          }
          List<String> usedClassesFqNames = new LinkReportReader(linkReportFile).fetchUsedClassesFqNames();          
          List<String> allClassesFqNames = new VisibleClassesCollector(currentProject).getVisibleSourceClassesFqNames();          
          monitor.worked(90);
          
          
          // Fill the text area
          for (final String fqName : allClassesFqNames) {
            if (!usedClassesFqNames.contains(fqName)) {
              Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                  List<String> excludedClassesInUI = getCurrentExcludedClassesFromUI();
                  
                  if (!excludedClassesInUI.contains(fqName)) {
                    excludedClassesTextArea.append(fqName);
                    excludedClassesTextArea.append("\n");                    
                  }          
                  
                  resort();
                }
              });                
            }
          }
          
          monitor.worked(100);
          
          return Status.OK_STATUS;
        } catch (Throwable t) {
          ErrorHandler.handle(t, "Error while running base compilation");
          return Status.CANCEL_STATUS;
        } finally {
          Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
              toggleButtonsEnable(true);              
            }
          });   
        }
      }
    }.schedule();
  }

  private void loadPart() {
    String excludedClassesStr = preferenceStore.getString("excludedClasses");    
    
    if (excludedClassesStr != null) {
      String[] split = excludedClassesStr.split("\\|");
      for (int i = 0; i < split.length; i++) {
        excludedClassesTextArea.append(split[i]);
        excludedClassesTextArea.append("\n");
      }
    }
  }
  
  private void resort() {
    List<String> currentExcludedClassesFromUI = getCurrentExcludedClassesFromUI();
    Collections.sort(currentExcludedClassesFromUI);
    
    excludedClassesTextArea.setText(StringUtils.EMPTY);
    
    for (String excludedClass : currentExcludedClassesFromUI) {
      excludedClassesTextArea.append(excludedClass);
      excludedClassesTextArea.append("\n");
    }
  }
  
  private List<String> getCurrentExcludedClassesFromUI() {
    List<String> result = new ArrayList<String>();
    
    StringTokenizer stringTokenizer = new StringTokenizer(excludedClassesTextArea.getText());

    while (stringTokenizer.hasMoreTokens()) {
      String excludedClass = stringTokenizer.nextToken().trim();
      if (StringUtils.isNotEmpty(excludedClass)) {
        result.add(excludedClass);
      }
    }
    
    return result;
  }
  
  private void savePart() {
    StringBuilder sb = new StringBuilder();
    StringTokenizer stringTokenizer = new StringTokenizer(excludedClassesTextArea.getText());

    while (stringTokenizer.hasMoreTokens()) {
      sb.append(stringTokenizer.nextToken());
      if (stringTokenizer.hasMoreElements()) {
        sb.append("|");
      }
    }

    preferenceStore.setValue("excludedClasses", sb.toString());
    try {
      ((ProjectPreferenceStore) preferenceStore).save();
    } catch (IOException e) {
      ErrorHandler.handle(e, "Error while saving excluded classes");
    }
  }

  public void createPartControl(Composite parent) {
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    layout.marginHeight = 5;
    layout.marginWidth = 10;   
    parent.setLayout(layout);

    Composite classesListAreaComposite = new Group(parent, SWT.NONE);
    GridLayout classesListAreaCompositeLayout = new GridLayout(1, false);
    classesListAreaCompositeLayout.marginHeight = 5;
    classesListAreaCompositeLayout.marginWidth = 5;
    classesListAreaCompositeLayout.marginBottom = 10;
    classesListAreaComposite.setLayout(classesListAreaCompositeLayout);
    classesListAreaComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    
    excludedClassesTextArea = new Text(classesListAreaComposite, SWT.MULTI | SWT.V_SCROLL);
    excludedClassesTextArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    excludedClassesTextArea.setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));

    Composite buttonsComposite = new Group(parent, SWT.NONE);
    GridLayout buttonsCompositeLayout = new GridLayout(3, false);
    buttonsCompositeLayout.marginHeight = 5;
    buttonsCompositeLayout.marginWidth = 5;
    buttonsCompositeLayout.marginBottom = 10;
    buttonsComposite.setLayout(buttonsCompositeLayout);
    buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false));

    generateButton = new Button(buttonsComposite, SWT.PUSH);
    generateButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
    generateButton.setText("Generate by link report");
    generateButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        generate();
      }
    });
    
    okButton = new Button(buttonsComposite, SWT.PUSH);
    okButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
    okButton.setText("OK");
    okButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        savePart();
        ExcludeClassesDialogShell.this.setVisible(false);
      }
    });

    cancelButton = new Button(buttonsComposite, SWT.PUSH);
    cancelButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
    cancelButton.setText("Cancel");
    cancelButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        ExcludeClassesDialogShell.this.setVisible(false);
      }
    });
  }

}
