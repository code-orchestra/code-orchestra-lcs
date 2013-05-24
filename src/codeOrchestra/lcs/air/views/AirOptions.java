package codeOrchestra.lcs.air.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * @author Oleg Chiruhin
 * @author Alexander Eliseyev
 */
public abstract class AirOptions {

	private List<AirOption> options = new ArrayList<AirOption>();
	private Group optionsGroup;

	public AirOptions(Composite parentComponent, IPreferenceStore preferenceStore, int numColumns) {
		optionsGroup = new Group(parentComponent, SWT.SHADOW_ETCHED_IN);
		optionsGroup.setText("Options");
		GridLayout optionsGroupLayout = new GridLayout(numColumns, false);
		optionsGroupLayout.marginHeight = 5;
		optionsGroupLayout.marginWidth = 5;
		optionsGroupLayout.marginBottom = 10;
		optionsGroup.setLayout(optionsGroupLayout);
		optionsGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		AirOptionSource[] optionSources = getOptionSources();
		for (int i = 0; i < optionSources.length; i++) {
			AirOptionSource optionSource = optionSources[i];
			AirOption aio = new AirOption(
					optionSource.getTitle(), 
					optionSource.getName(), 
					optionSource.getCompilerSetting(), 
					optionSource.getOptionType(),
					optionsGroup,
					preferenceStore, 
					optionSource.getOptionType().isFileType() ? numColumns - 2 : numColumns - 1);
			options.add(aio);
			aio.updateComposite();
		}
	}
	
	protected abstract AirOptionSource[] getOptionSources();

	public void saveOptions() {
		for (AirOption op : options) {
			op.getEditor().store();
		}
	}

	public void loadOptions() {
		for (AirOption op : options) {
			op.getEditor().load();
		}
	}

	public List<AirOption> getOptions() {
		return options;
	}

	public void setOptions(List<AirOption> options) {
		this.options = options;
	}

}
