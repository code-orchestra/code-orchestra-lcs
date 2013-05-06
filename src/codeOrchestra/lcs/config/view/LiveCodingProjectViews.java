package codeOrchestra.lcs.config.view;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import codeOrchestra.lcs.ApplicationWorkbenchWindowAdvisor;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.views.AirIosSettingsView;
import codeOrchestra.lcs.views.FCSHConsoleView;
import codeOrchestra.lcs.views.LiveCodingProjectPartView;
import codeOrchestra.lcs.views.LiveCodingSettingsView;
import codeOrchestra.lcs.views.elements.AirIosOptions;
import codeOrchestra.lcs.views.elements.ViewHelper;

/**
 * @author Alexander Eliseyev
 */
public final class LiveCodingProjectViews {

  @SuppressWarnings("serial")
  private static Set<String> lcpViewIDs = new LinkedHashSet<String>() {{
    add("LCS.sourcesView");
    add("LCS.liveCodingView");
    add("LCS.compilerView");
    add("LCS.AirIosSettingsView");
  }};
  
  public static boolean isLiveCodingView(IViewReference viewReference) {
    return lcpViewIDs.contains(viewReference.getId());
  }
  
  @SuppressWarnings("unchecked")
  public static boolean validateProjectViewsState(IWorkbenchWindow window, LCSProject project) {
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
      
      MessageDialog.openError(window.getShell(), "Invalid settings", "Can't start a livecoding session due to invalid settings:\n\n" + sb.toString());
      
      return false;
    }
    
    return true;
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
          if (view != null && view instanceof AirIosSettingsView) {
              @SuppressWarnings("rawtypes")
              AirIosSettingsView airIosSettingsView = (AirIosSettingsView) view;
              airIosSettingsView.savePart();
            }
        }
      }
    }
  }
  
  public static void openProjectViews(IWorkbenchWindow window, LCSProject project) throws PartInitException {
    boolean activated = false;
    for (String viewId : lcpViewIDs) {
      if (AirIosSettingsView.ID.equals(viewId)) {
    	  LiveCodingSettingsView lcsv = (LiveCodingSettingsView) ViewHelper.getView(window,LiveCodingSettingsView.ID);
    	  if (null!=lcsv && !lcsv.isAirIosSelected()) {
    		  AirIosSettingsView aisv = (AirIosSettingsView) ViewHelper.getView(window,AirIosSettingsView.ID);
    		  if (null!=aisv) {
    			  window.getActivePage().hideView(aisv);
    		  }
    		  continue;
    	  }
      }
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
    AirIosSettingsView aisv = (AirIosSettingsView) ViewHelper.getView(activeWindow, AirIosSettingsView.ID);
    if (null!=aisv) {
    	aisv.getAirIosFileTree().removeFileMonitor();
    }
    
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
