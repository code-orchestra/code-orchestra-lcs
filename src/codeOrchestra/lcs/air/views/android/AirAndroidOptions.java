package codeOrchestra.lcs.air.views.android;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;

import codeOrchestra.lcs.air.views.AirOptionSource;
import codeOrchestra.lcs.air.views.AirOptionType;
import codeOrchestra.lcs.air.views.AirOptions;

/**
 * @author Alexander Eliseyev
 */
public class AirAndroidOptions extends AirOptions {
  
  private static final AirOptionSource[] optionSources = new AirOptionSource[] {
    new AirOptionSource("AIR SDK", "air-sdk", null, AirOptionType.DIRECTORY),
    new AirOptionSource("keystore", AirOptionType.FILE),
    new AirOptionSource("storepass", AirOptionType.PASSWORD),     
  };

  public AirAndroidOptions(Composite parentComponent, IPreferenceStore preferenceStore, int numColumns) {
    super(parentComponent, preferenceStore, numColumns);
  }
  
  @Override
  protected AirOptionSource[] getOptionSources() {
    return optionSources;
  }

}
