package codeOrchestra.lcs.views.elements;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;

import scala.actors.threadpool.Arrays;

public class AirIosOptions {
	private String[][] optionSources = new String[][] {
			of("provisioning-profile"),
			of("keystore"),
			op("storepass")
	}; 

	private static String[] o(String name, String widgetType) {
		return new String[] {"-"+name, name, "-"+name, widgetType};
	}
	
	private static String[] os(String name) {
		return new String[] {"-"+name, name, "-"+name, "string"};
	}
	
	private static String[] op(String name) {
		return new String[] {"-"+name, name, "-"+name, "password"};
	}
	
	private static String[] of(String name) {
		return new String[] {"-"+name, name, "-"+name, "file"};
	}
	
	private static String[] od(String name) {
		return new String[] {"-"+name, name, "-"+name, "directory"};
	}

	List<AirIosOption> options = new ArrayList<AirIosOption>();
	private Composite parentComponent;
	private IPreferenceStore preferenceStore; 
	private int numColumns;
	private Group optionsGroup;

	public AirIosOptions(Composite parentComponent, IPreferenceStore preferenceStore, int numColumns) {
		this.parentComponent = parentComponent;
		this.preferenceStore = preferenceStore;
		this.numColumns = numColumns;

		optionsGroup = new Group(parentComponent, SWT.SHADOW_ETCHED_IN);
		optionsGroup.setText("Options");
		GridLayout optionsGroupLayout = new GridLayout(numColumns, false);
		optionsGroupLayout.marginHeight = 5;
		optionsGroupLayout.marginWidth = 5;
		optionsGroupLayout.marginBottom = 10;
		optionsGroup.setLayout(optionsGroupLayout);
		optionsGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));	
//		optionsGroup.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));

		for(int i=0; i<optionSources.length; i++) {
			String[] op = optionSources[i];
			AirIosOption aio = new AirIosOption(op[0], op[1], op[2], op[3], optionsGroup, preferenceStore, op[3].equals("file")||op[3].equals("directory")?numColumns-2:numColumns-1);			
			options.add(aio);
			aio.updateComposite();
		}
	}
	
	
	
	public void saveOptions() {
		for (AirIosOption op : options) {
			op.getEditor().store();
		}
	}
	
	public void loadOptions() {
		for (AirIosOption op : options) {
			op.getEditor().load();
		}
	}

	public String[][] getOptionSources() {
		return optionSources;
	}

	public void setOptionSources(String[][] optionSources) {
		this.optionSources = optionSources;
	}

	public List<AirIosOption> getOptions() {
		return options;
	}

	public void setOptions(List<AirIosOption> options) {
		this.options = options;
	}


}
