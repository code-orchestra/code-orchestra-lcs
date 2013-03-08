package codeOrchestra.lcs;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import codeOrchestra.lcs.views.SourceSettingsView;

/**
 * @author Alexander Eliseyev
 */
public class Perspective implements IPerspectiveFactory {

	/**
	 * The ID of the perspective as specified in the extension.
	 */
	public static final String ID = "LCS.perspective";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);	

		IFolderLayout folder = layout.createFolder("liveCoding", IPageLayout.TOP, 0.5f, editorArea);
		folder.addPlaceholder(SourceSettingsView.ID + ":*");
	}
}
