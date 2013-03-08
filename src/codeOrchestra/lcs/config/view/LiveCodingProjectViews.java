package codeOrchestra.lcs.config.view;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import codeOrchestra.lcs.project.LCSProject;

/**
 * @author Alexander Eliseyev
 */
public final class LiveCodingProjectViews {

  private static Set<String> lcpViewIDs = new HashSet<String>() {{
    add("LCS.sourcesView");
    add("LCS.liveCodingView");
    add("LCS.compilerView");
  }};
  
  public static boolean isLiveCodingView(IViewReference viewReference) {
    return lcpViewIDs.contains(viewReference.getId());
  }
  
  public static void openViews(LCSProject project) throws PartInitException {
    
  }
  
  public static void closeAllViews() {
    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    if (page != null) {
        IViewReference[] viewReferences = page.getViewReferences();
        for (IViewReference ivr : viewReferences) {
            if (LiveCodingProjectViews.isLiveCodingView(ivr)) {
                page.hideView(ivr);
            }
        }
    }
  }
  
}
