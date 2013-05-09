package codeOrchestra.lcs.config.view;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
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
  
  @SuppressWarnings("unchecked")
  private static boolean validateProjectViewsState(IWorkbenchWindow window, LCSProject project) {
    List<String> errors = new ArrayList<String>();
    
    IViewReference[] viewReferences = window.getActivePage().getViewReferences();
    for (String viewId : lcpViewIDs) {      
      for (int i = 0; i < viewReferences.length; i++) {
        if (viewReferences[i].getId().equals(viewId)) {
          IViewPart view = viewReferences[i].getView(false);
          if (view != null && view instanceof LiveCodingProjectPartView) {
            @SuppressWarnings("rawtypes")
            LiveCodingProjectPartView liveCodingProjectPartView = (LiveCodingProjectPartView) view;
            errors.addAll(liveCodingProjectPartView.validate());
          }
        }
      }
    }
    
    if (!errors.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      for (String errorMessage : errors) {
        sb.append(errorMessage).append("\n");
      }
      
      MessageDialog.openError(window.getShell(), "Invalid settings", "Can't save the project due to invalid settings:\n\n" + sb.toString());
      
      return false;
    }
    
    return true;
  }
  
  public static boolean saveProjectViewsState(IWorkbenchWindow window, LCSProject project) {
    if (!validateProjectViewsState(window, project)) {
      return false;
    }
    
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
    
    return true;
  }
  
  public static void openProjectViews(IWorkbenchWindow window, LCSProject project) throws PartInitException {
    boolean activated = false;
    for (String viewId : lcpViewIDs) {      
      window.getActivePage().showView(viewId, project.getName(), !activated ? IWorkbenchPage.VIEW_ACTIVATE : IWorkbenchPage.VIEW_CREATE);
      activated = true;
    }
    
    ApplicationWorkbenchWindowAdvisor.sharedInstance.setTitle("Code Orchestra Livecoding Tool - " + project.getName() + " - " + project.getPath());
  }
  
  
  public static boolean closeProjectViews() {
    if (ApplicationWorkbenchWindowAdvisor.sharedInstance == null) {
      return false;
    }
    
    ApplicationWorkbenchWindowAdvisor.sharedInstance.setTitle("Code Orchestra Livecoding Tool");
    
    IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    IWorkbenchPage page = activeWindow.getActivePage();
    if (page != null) {
        IViewReference[] viewReferences = page.getViewReferences();
        for (IViewReference ivr : viewReferences) {
            if (LiveCodingProjectViews.isLiveCodingView(ivr)) {
                page.hideView(ivr);
            }
        }
    }
    
    return true;
  }
  
}
