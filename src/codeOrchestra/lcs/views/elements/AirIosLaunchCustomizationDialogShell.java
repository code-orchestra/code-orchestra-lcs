package codeOrchestra.lcs.views.elements;

import java.io.IOException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import codeOrchestra.lcs.air.AirIosIpaBuildScriptGenerator;
import codeOrchestra.lcs.config.view.LiveCodingProjectViews;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.views.LiveCodingSettingsView;

public class AirIosLaunchCustomizationDialogShell extends ModalSettingsDialogShell {
	  private AirIosOptions airIosOptions;
	  private AirIosFileTree airIosFileTree;
	  private IPreferenceStore preferenceStore;
	  private IWorkbenchWindow window;
	  
	  private boolean submitted = false;

	public AirIosLaunchCustomizationDialogShell(IWorkbenchWindow window, IPreferenceStore preferenceStore) {
		super("Apple iOS: customize launch",800,600);
		this.window = window;;
		this.preferenceStore = preferenceStore;
		this.getBaseDialogShell().addDisposeListener(new DisposeListener() {

	        public void widgetDisposed(DisposeEvent e) {
	        	IWorkbenchWindow window = AirIosLaunchCustomizationDialogShell.this.window;
	            if (null!=airIosFileTree) {
	            	airIosFileTree.removeFileMonitor();
	            }
	        }
	    });
		createPartControl(this.getBaseDialogShell());
	}
	
	public void runGeneration() {
        if (submitted) {
        	 // 1 - save
	        LCSProject currentProject = LCSProject.getCurrentProject();
	        LiveCodingProjectViews.saveProjectViewsState(window, currentProject);
	        currentProject.save();
	        
	        // 2 - generate
	        String scriptPath = null;
	        try {          
	          scriptPath = new AirIosIpaBuildScriptGenerator(currentProject, currentProject.getName(),window).generate(airIosOptions,airIosFileTree);  
	        } catch (IOException e1) {
	          // TODO Auto-generated catch block
	          e1.printStackTrace();
	        }
	        
	        // 3 - update address
	        LiveCodingSettingsView lcsv = (LiveCodingSettingsView) ViewHelper.getView(window, LiveCodingSettingsView.ID);
	        if (null!=lcsv) {
	        	lcsv.setAirIosScriptEditorValue(scriptPath);
	        }
        }
	}
	
	public void savePart() {
		airIosOptions.saveOptions();
		airIosFileTree.saveOptions();
	  }
	
	  public void createPartControl(Composite parent) {
	    GridLayout layout = new GridLayout();
	    layout.numColumns = 1;
	    layout.marginHeight = 5;
	    layout.marginWidth = 10;	 
	    parent.setLayout(layout);

	    airIosOptions = new AirIosOptions(parent, getPreferenceStore(), 3);
		airIosOptions.loadOptions();

		airIosFileTree = new AirIosFileTree(parent,getPreferenceStore());
		airIosFileTree.loadOptions();
		
		Composite compos = new Group(parent, SWT.NONE);
		GridLayout optionsGroupLayout = new GridLayout(2, false);
		optionsGroupLayout.marginHeight = 5;
		optionsGroupLayout.marginWidth = 5;
		optionsGroupLayout.marginBottom = 10;
		compos.setLayout(optionsGroupLayout);
		compos.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false));	

	    Button ok = new Button(compos, SWT.PUSH);
	    ok.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
	    ok.setText("Generate");
	    ok.addSelectionListener(new SelectionAdapter() {
	      @Override
	      public void widgetSelected(SelectionEvent e) {
	    	  savePart();
	    	  AirIosLaunchCustomizationDialogShell.this.submitted=true;
	    	  runGeneration();
	    	  AirIosLaunchCustomizationDialogShell.this.setVisible(false);
	      }
	    });
	    
	    Button cancel = new Button(compos, SWT.PUSH);
	    cancel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
	    cancel.setText("Cancel");
	    cancel.addSelectionListener(new SelectionAdapter() {
	      @Override
	      public void widgetSelected(SelectionEvent e) {
	          savePart();
	    	  AirIosLaunchCustomizationDialogShell.this.submitted=false;
	    	  AirIosLaunchCustomizationDialogShell.this.setVisible(false);
	      }
	    });
		
	  }

	public AirIosOptions getAirIosOptions() {
		return airIosOptions;
	}

	public void setAirIosOptions(AirIosOptions airIosOptions) {
		this.airIosOptions = airIosOptions;
	}

	public AirIosFileTree getAirIosFileTree() {
		return airIosFileTree;
	}

	public void setAirIosFileTree(AirIosFileTree airIosFileTree) {
		this.airIosFileTree = airIosFileTree;
	}

	public IPreferenceStore getPreferenceStore() {
		return preferenceStore;
	}

	public void setPreferenceStore(IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
	}
	  
	  
	  
}
