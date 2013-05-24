package codeOrchestra.lcs.air;

import java.io.File;
import java.io.IOException;

import org.eclipse.ui.IWorkbenchWindow;

import codeOrchestra.lcs.air.views.AirFileTree;
import codeOrchestra.lcs.air.views.AirOptions;
import codeOrchestra.lcs.project.LCSProject;

/**
 * @author Alexander Eliseyev
 */
public abstract class AirBuildScriptGenerator {

  protected LCSProject project;
  protected String appName;
  protected IWorkbenchWindow window;

  public AirBuildScriptGenerator(LCSProject project) {
    this(project, null);
  }

  public AirBuildScriptGenerator(LCSProject project, IWorkbenchWindow window) {
    this.project = project;
    this.appName = project.getName();
    this.window = window;
  }
  
  public File getScriptPath(LCSProject project) {
    return new File(project.getOutputDir(), getScriptFileName());
  }

  public File getDescScriptPath(LCSProject project) {
    return new File(project.getOutputDir(), appName + "-app.xml");
  }

  protected abstract String getScriptFileName();
  
  public abstract String generate(AirOptions aioParent, AirFileTree fileTree) throws IOException;

  protected static boolean checkChild(File maybeChild, File possibleParent) throws IOException {
    final File parent = possibleParent.getCanonicalFile();
    if (!parent.exists() || !parent.isDirectory()) {
      // this cannot possibly be the parent
      return false;
    }

    File child = maybeChild.getCanonicalFile();
    while (child != null) {
      if (child.equals(parent)) {
        return true;
      }
      child = child.getParentFile();
    }
    // No match found, and we've hit the root directory
    return false;
  }

}
