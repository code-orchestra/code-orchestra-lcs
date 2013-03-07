package codeOrchestra.lcs;

import java.io.IOException;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.PathEditor;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import codeOrchestra.lcs.config.view.LiveConfigViewStack;
import codeOrchestra.lcs.config.view.PathEditorEx;

public class LiveConfigurationView extends ViewPart {

	public static final String ID = "LCS.view";

	private PathEditor sourcePathsEditor;
	private PathEditor libraryPathsEditor;
	private StringFieldEditor mainClassEditor;
	private DirectoryFieldEditor flexSDKPathEditor;

	@Override
	public void init(IViewSite site) throws PartInitException {
	    super.init(site);
	    setPartName(LiveConfigViewStack.lastName);
	}
	
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		parent.setLayout(layout);
		
		Composite banner = new Composite(parent, SWT.NONE);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		banner.setLayoutData(layoutData);
		
		final PreferenceStore preferenceStore = new PreferenceStore("/Users/buildserver/TMP/bugaga.properties");
		try {
			preferenceStore.load();
		} catch (IOException e) {
			throw new RuntimeException("Error loading live coding configuration", e);
		}
		
		// Controls

		Composite pathsComposite = new Composite(banner, SWT.NONE);
		pathsComposite.setLayout(new GridLayout());
		pathsComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		
		Composite flexPathComposite = new Composite(pathsComposite, SWT.NONE);
		flexPathComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		flexSDKPathEditor = new DirectoryFieldEditor("flexSDKPath", "Flex SDK Path:", flexPathComposite);
		flexSDKPathEditor.setPreferenceStore(preferenceStore);
		flexSDKPathEditor.load();

		Composite mainClassComposite = new Composite(pathsComposite, SWT.NONE);
		mainClassComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		mainClassEditor = new StringFieldEditor("mainClass", "Main class:", mainClassComposite);
		mainClassEditor.setPreferenceStore(preferenceStore);
		mainClassEditor.load();
	
		sourcePathsEditor = new PathEditorEx("sourcePaths", "Source Paths:", "Choose a Source Path", banner, true);
		sourcePathsEditor.setPreferenceStore(preferenceStore);
		sourcePathsEditor.load();
		
		libraryPathsEditor = new PathEditorEx("libraryPaths", "Library Paths:", "Choose a Library Path", banner, false);
		libraryPathsEditor.setPreferenceStore(preferenceStore);
		libraryPathsEditor.load();
		
		Button buttton = new Button(banner, SWT.PUSH);
		buttton.setText("Save");
		buttton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				sourcePathsEditor.store();
				libraryPathsEditor.store();
				mainClassEditor.store();
				flexSDKPathEditor.store();
				
				try {
					preferenceStore.save();
				} catch (IOException e) {
					throw new RuntimeException("Can't save the preferences", e);
				}
			}			
		});
	}

	public void setFocus() {
		sourcePathsEditor.setFocus();
	}
}
