package codeOrchestra.lcs;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.PathEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import codeOrchestra.lcs.config.view.PathEditorEx;

public class View extends ViewPart {

	public static final String ID = "LCS.view";

	private PathEditor sourcePathsEditor;
	private PathEditor libraryPathsEditor;
	private StringFieldEditor mainClassEditor;
	private DirectoryFieldEditor flexSDKPathEditor;
	
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
		
		Composite flexPathComposite = new Composite(pathsComposite, SWT.NONE);
		flexPathComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		flexSDKPathEditor = new DirectoryFieldEditor("flexSDKPath", "Flex SDK Path:", flexPathComposite);
		flexSDKPathEditor.setPreferenceStore(PlatformUI.getPreferenceStore());
		flexSDKPathEditor.load();

		Composite mainClassComposite = new Composite(pathsComposite, SWT.NONE);
		mainClassComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		mainClassEditor = new StringFieldEditor("mainClass", "Main class:", mainClassComposite);
		mainClassEditor.setPreferenceStore(PlatformUI.getPreferenceStore());
		mainClassEditor.load();
	
		sourcePathsEditor = new PathEditorEx("sourcePaths", "Source Paths:", "Choose a Source Path", banner, true);
		sourcePathsEditor.setPreferenceStore(PlatformUI.getPreferenceStore());
		sourcePathsEditor.load();
		
		libraryPathsEditor = new PathEditorEx("libraryPaths", "Library Paths:", "Choose a Library Path", banner, false);
		libraryPathsEditor.setPreferenceStore(PlatformUI.getPreferenceStore());
		libraryPathsEditor.load();
		
		Button buttton = new Button(banner, SWT.PUSH);
		buttton.setText("Save");
		buttton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sourcePathsEditor.store();
				libraryPathsEditor.store();
				mainClassEditor.store();
				flexSDKPathEditor.store();
			}			
		});
	}

	public void setFocus() {
		sourcePathsEditor.setFocus();
	}
}
