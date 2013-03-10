package codeOrchestra.lcs.logging;

import org.eclipse.swt.graphics.Image;

/**
 * @author Alexander Eliseyev
 */
public enum Level {
  
  OFF,
  FATAL,
  ERROR(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/messages/error.png").createImage()),
  WARN(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/messages/warning.png").createImage()),
  INFO(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/messages/information.png").createImage()),
  DEBUG,
  TRACE,
  ALL;
  
  private Image image;
  
  private Level() {    
  }
  
  private Level(Image image) {
    this.image = image; 
  }

  public Image getImage() {
    return image;
  }
  
}
