package codeOrchestra.lcs.views;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.part.ViewPart;

import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.project.ProjectSettingsPart;

/**
 * @author Alexander Eliseyev
 */
public abstract class LiveCodingProjectPartView<P extends ProjectSettingsPart> extends ViewPart {
  
  public abstract void savePart();  
  
  protected IPreferenceStore getPreferenceStore() {
    return LCSProject.getCurrentProject().getPreferenceStore();
  }

}
