package codeOrchestra.lcs.config.view;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import codeOrchestra.lcs.ApplicationWorkbenchWindowAdvisor;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.views.LiveCodingProjectPartView;

/**
 * @author Alexander Eliseyev
 */
public final class LiveCodingProjectViews {

  @SuppressWarnings("serial")
  private static Set<String> lcpViewIDs = new LinkedHashSet<String>() {{
    add("LCS.sourcesView");
    add("LCS.liveCodingView");
    add("LCS.compilerView");
  }};
  
  public static boolean isLiveCodingView(IViewReference viewReference) {
    return lcpViewIDs.contains(viewReference.getId());
  }
  
  public static void saveProjectViewsState(IWorkbenchWindow window, LCSProject project) {
    IViewReference[] viewReferences = window.getActivePage().getViewReferences();
    for (String viewId : lcpViewIDs) {      
      for (int i = 0; i < viewReferences.length; i++) {
        if (viewReferences[i].getId().equals(viewId)) {
          IViewPart view = viewReferences[i].getView(false);
          if (view != null && view instanceof LiveCodingProjectPartView) {
            @SuppressWarnings("rawtypes")
            LiveCodingProjectPartView liveCodingProjectPartView = (LiveCodingProjectPartView) view;
            liveCodingProjectPartView.savePart();
          }
        }
      }
    }
  }
  
  public static void openProjectViews(IWorkbenchWindow window, LCSProject project) throws PartInitException {
    boolean activated = false;
    for (String viewId : lcpViewIDs) {
      window.getActivePage().showView(viewId, project.getName(), !activated ? IWorkbenchPage.VIEW_ACTIVATE : IWorkbenchPage.VIEW_CREATE);
      activated = true;
    }
    
    ApplicationWorkbenchWindowAdvisor.sharedInstance.setTitle("CodeOrchestra Live Coding Server - " + project.getName() + " - " + project.getPath());
  }
  
  public static void closeProjectViews() {
    ApplicationWorkbenchWindowAdvisor.sharedInstance.setTitle("CodeOrchestra Live Coding Server");
    
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
