package codeOrchestra.lcs.views.elements;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import codeOrchestra.lcs.config.view.LiveCodingProjectViews;
import codeOrchestra.lcs.project.LCSProject;

public class ViewHelper {
	public static IViewPart getView(IWorkbenchWindow window, String viewId) {
	    IViewReference[] refs = window.getActivePage().getViewReferences();
	    for (IViewReference viewReference : refs) {
	        if (viewReference.getId().equals(viewId)) {
	            return viewReference.getView(true);
	        }
	    }
	    return null;
	}  
	public static void setViewVisible(String viewId, boolean isVisible, LCSProject project, boolean activate) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	    if (page != null) {
	        IViewReference[] viewReferences = page.getViewReferences();
	        for (IViewReference ivr : viewReferences) {
	            if (ivr.getId().equals(viewId)) {
	            	if (isVisible) {
	            		try {
		            		if (null!=project) {
		            			page.showView(viewId, project.getName(), activate ? IWorkbenchPage.VIEW_ACTIVATE : IWorkbenchPage.VIEW_CREATE);
		            		} else {
		            			page.showView(viewId);
		            		}
	            		} catch (Exception e) {
	            			e.printStackTrace();
	            		}
	            	} else {
	            		page.hideView(ivr);
	            	}
	            }
	        }
	    }
	}
}
