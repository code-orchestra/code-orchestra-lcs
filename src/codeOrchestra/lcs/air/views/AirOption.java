package codeOrchestra.lcs.air.views;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import codeOrchestra.lcs.air.views.utils.PasswordFieldEditor;

/**
 * @author Oleg Chiruhin
 * @author Alexander Eliseyev
 */
public class AirOption {

	private String title;

	private String name;
	private String compilerSetting;
	private Composite composite;
	private Composite parentComponent;
	private IPreferenceStore preferenceStore;
	private int horizontalIndent = 1;
	private StringFieldEditor editor;
	private AirOptionType optionType;

	public AirOption(String title, String name, String compilerSetting, AirOptionType optionType,
			Composite parentComponent, IPreferenceStore preferenceStore, int horizontalIndent) {
		this.title = title;
		this.name = name;
		this.compilerSetting = compilerSetting;
		this.parentComponent = parentComponent;
		this.preferenceStore = preferenceStore;
		this.horizontalIndent = horizontalIndent;
		this.optionType = optionType;
	}
	
	public void updateComposite() {
		Label label = new Label(getParentComponent(), SWT.NONE);
		label.setText(getTitle());
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		setComposite(new Composite(getParentComponent(), SWT.NONE));
		GridData editorCompositeGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		editorCompositeGridData.horizontalIndent = 1;
		editorCompositeGridData.horizontalSpan = getHorizontalIndent();
		getComposite().setLayoutData(editorCompositeGridData);

		final StringFieldEditor currEditor;
		if (optionType != AirOptionType.PASSWORD) {
			currEditor = new StringFieldEditor(getName(), "", getComposite());
		} else {
			currEditor = new PasswordFieldEditor(getName(), "", getComposite());
		}

		setEditor(currEditor);
		getEditor().setEnabled(true, getComposite());
		getEditor().setPreferenceStore(getPreferenceStore());

		if (optionType.isFileType()) {
			Button btn = new Button(getParentComponent(), SWT.PUSH);
			btn.setText("Browse...");
			btn.setSize(btn.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			
			btn.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (optionType == AirOptionType.DIRECTORY) {
						currEditor.setStringValue(getDirPath("Select directory"));
					} else if (optionType == AirOptionType.FILE) {
						currEditor.setStringValue(getFilePath("Select file"));
					}
				}
			});
		}
	}

	private Shell getShell() {
		return getParentComponent().getShell();
	}

	protected String getFilePath(String text) {
		FileDialog fileDialog = new FileDialog(getShell(), SWT.SHEET);
		fileDialog.setText(text);
		String file = fileDialog.open();

		if (file != null) {
			file = file.trim();
			if (file.length() == 0) {
				return "";
			}
		}
		return file;
	}

	protected String getDirPath(String text) {
		DirectoryDialog fileDialog = new DirectoryDialog(getShell(), SWT.SHEET);
		fileDialog.setText(text);
		String file = fileDialog.open();

		if (file != null) {
			file = file.trim();
			if (file.length() == 0) {
				return "";
			}
		}
		return file;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StringFieldEditor getEditor() {
		return editor;
	}

	public void setEditor(StringFieldEditor editor) {
		this.editor = editor;
	}

	public int getHorizontalIndent() {
		return horizontalIndent;
	}

	public void setHorizontalIndent(int horizontalIndent) {
		this.horizontalIndent = horizontalIndent;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCompilerSetting() {
		return compilerSetting;
	}

	public void setCompilerSetting(String compilerSetting) {
		this.compilerSetting = compilerSetting;
	}

	public String getValue() {
		return getEditor().getStringValue();
	}

	public Composite getComposite() {
		return composite;
	}

	public void setComposite(Composite composite) {
		this.composite = composite;
	}

	public Composite getParentComponent() {
		return parentComponent;
	}

	public void setParentComponent(Composite parentComponent) {
		this.parentComponent = parentComponent;
	}

	public IPreferenceStore getPreferenceStore() {
		return preferenceStore;
	}

	public void setPreferenceStore(IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
	}

	public AirOptionType getOptionType() {
		return optionType;
	}

	public void setOptionType(AirOptionType optionType) {
		this.optionType = optionType;
	}

}
