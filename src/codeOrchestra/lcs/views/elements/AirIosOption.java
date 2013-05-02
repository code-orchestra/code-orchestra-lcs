package codeOrchestra.lcs.views.elements;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class AirIosOption {
	private String title;
	private String name;
	private String compilerSetting;
	private String value;
	private Composite composite;
	private Composite parentComponent;
	private IPreferenceStore preferenceStore;
	private int horizontalIndent = 1;
	private FieldEditor editor;		
	
	public void updateComposite() {
	    Label label = new Label(getParentComponent(), SWT.NONE);
	    label.setText(getTitle());
	    label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
	    setComposite(new Composite(getParentComponent(), SWT.NONE));
	    GridData editorCompositeGridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
	    editorCompositeGridData.horizontalIndent = getHorizontalIndent();
	    getComposite().setLayoutData(editorCompositeGridData);
	    setEditor(new StringFieldEditor(getName(), "", getComposite()));
	    getEditor().setEnabled(false, getComposite());
	    getEditor().setPreferenceStore(getPreferenceStore());		
	}
	
	public AirIosOption(String title, String name, String compilerSetting, Composite parentComponent, IPreferenceStore preferenceStore, int horizontalIndent) {
		this.title = title;
		this.name = name;
		this.compilerSetting = compilerSetting;
		this.parentComponent = parentComponent;
		this.preferenceStore = preferenceStore;
		this.horizontalIndent = horizontalIndent;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public FieldEditor getEditor() {
		return editor;
	}


	public void setEditor(FieldEditor editor) {
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
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
	
	
}
