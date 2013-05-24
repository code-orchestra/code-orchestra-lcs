package codeOrchestra.lcs.air.views;

import java.io.IOException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbenchWindow;

import codeOrchestra.lcs.air.AirBuildScriptGenerator;
import codeOrchestra.lcs.air.views.utils.ModalSettingsDialogShell;
import codeOrchestra.lcs.config.view.LiveCodingProjectViews;
import codeOrchestra.lcs.errorhandling.ErrorHandler;
import codeOrchestra.lcs.project.LCSProject;

/**
 * @author Oleg Chiruhin
 * @author Alexander Eliseyev
 */
public abstract class AirLaunchCustomizationDialogShell extends ModalSettingsDialogShell {

	private IPreferenceStore preferenceStore;
	private IWorkbenchWindow window;
	
	private boolean submitted;
	
	private AirFileTree airFileTree;
	private AirOptions airOptions;

	public AirLaunchCustomizationDialogShell(IWorkbenchWindow window, IPreferenceStore preferenceStore, String title) {
		super(title, 800, 600);
		this.window = window;
		this.preferenceStore = preferenceStore;
		this.getBaseDialogShell().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (null != airFileTree) {
					airFileTree.removeFileMonitor();
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
				scriptPath = createBuildScriptGenerator(currentProject, window).generate(airOptions, airFileTree);
			} catch (IOException e) {
				ErrorHandler.handle(e, "Error while generating AIR build script");
			}

			// 3 - update address
			updateScriptEditorValue(window, scriptPath);
		}
	}

	protected abstract void updateScriptEditorValue(IWorkbenchWindow window, String scriptPath);
	
	protected abstract AirBuildScriptGenerator createBuildScriptGenerator(LCSProject project, IWorkbenchWindow window);
	
	protected abstract AirOptions createOptions(Composite parent);

	public void savePart() {
		airOptions.saveOptions();
		airFileTree.saveOptions();
	}

	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		parent.setLayout(layout);

		airOptions = createOptions(parent);
		airOptions.loadOptions();

		airFileTree = new AirFileTree(parent, getPreferenceStore());
		airFileTree.loadOptions();

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
				AirLaunchCustomizationDialogShell.this.submitted = true;
				runGeneration();
				airFileTree.removeFileMonitor();
				AirLaunchCustomizationDialogShell.this.setVisible(false);
			}
		});

		Button cancel = new Button(compos, SWT.PUSH);
		cancel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		cancel.setText("Cancel");
		cancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				savePart();
				AirLaunchCustomizationDialogShell.this.submitted = false;
				airFileTree.removeFileMonitor();
				AirLaunchCustomizationDialogShell.this.setVisible(false);
			}
		});
	}

	public IPreferenceStore getPreferenceStore() {
		return preferenceStore;
	}

	public void setPreferenceStore(IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
	}

}
