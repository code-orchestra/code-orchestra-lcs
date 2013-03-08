package codeOrchestra.lcs.views;

import org.eclipse.ui.part.ViewPart;

import codeOrchestra.lcs.project.ProjectSettingsPart;

/**
 * @author Alexander Eliseyev
 */
public abstract class LiveCodingProjectPartView<P extends ProjectSettingsPart> extends ViewPart {
  
  protected abstract void savePart();  

}
