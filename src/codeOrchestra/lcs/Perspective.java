package codeOrchestra.lcs;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import codeOrchestra.lcs.messages.MessagesManager;
import codeOrchestra.lcs.views.CompilerSettingsView;
import codeOrchestra.lcs.views.LiveCodingSettingsView;
import codeOrchestra.lcs.views.MessagesView;
import codeOrchestra.lcs.views.SourceSettingsView;

/**
 * @author Alexander Eliseyev
 */
public class Perspective implements IPerspectiveFactory {

  private String name;
  
	/**
	 * The ID of the perspective as specified in the extension.
	 */
	public static final String ID = "LCS.perspective";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);	

		// Settings
		IFolderLayout liveCodingFolder = layout.createFolder("liveCoding", IPageLayout.TOP, 0.75f, editorArea);
		liveCodingFolder.addPlaceholder(SourceSettingsView.ID + ":*");
		liveCodingFolder.addPlaceholder(LiveCodingSettingsView.ID + ":*");
		liveCodingFolder.addPlaceholder(CompilerSettingsView.ID + ":*");
		
		// Messages
		IFolderLayout messagesFolder = layout.createFolder("messages", IPageLayout.BOTTOM, 0.25f, editorArea);
		messagesFolder.addPlaceholder(MessagesView.ID + ":*");		
		MessagesManager.init(messagesFolder);
		MessagesManager.getInstance().addTab("Main");
	}

  public String getName() {
    return name;
  }
	
	
	
}
