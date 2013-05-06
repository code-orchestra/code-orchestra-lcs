package codeOrchestra.lcs.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import codeOrchestra.lcs.project.SourceSettings;
import codeOrchestra.lcs.views.elements.AirIosFileTree;
import codeOrchestra.lcs.views.elements.AirIosOptions;
import codeOrchestra.utils.PathEditorEx;

public class AirIosSettingsView extends LiveCodingProjectPartView<SourceSettings> {
	  
	  public static final String ID = "LCS.AirIosSettingsView";
	  
	  private AirIosOptions airIosOptions;
	  private AirIosFileTree airIosFileTree;

	  @Override
	  public void savePart() {
		airIosOptions.saveOptions();
		airIosFileTree.saveOptions();
	  }
	  
	  @Override
	  public List<String> validate() {
	    List<String> errors = new ArrayList<String>();
	    
//	    List<String> sourcePaths = sourcePathsEditor.getItems();
//	    if (sourcePaths.isEmpty()) {
//	      errors.add("No source paths specified");
//	    }
//	    
//	    for (String sourcePath : sourcePaths) {
//	      if (!new File(sourcePath).exists()) {
//	        errors.add("Invalid source path " + sourcePath);
//	      }
//	    }
	    
	    return errors;
	  }

	  @Override
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

	@Override
	  public void setFocus() {    
	  }

	}
