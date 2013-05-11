package codeOrchestra.lcs;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import codeOrchestra.lcs.messages.MessagesManager;
import codeOrchestra.lcs.session.SessionViewManager;
import codeOrchestra.lcs.views.CompilerSettingsView;
import codeOrchestra.lcs.views.FCSHConsoleView;
import codeOrchestra.lcs.views.LiveCodingSettingsView;
import codeOrchestra.lcs.views.MessagesView;
import codeOrchestra.lcs.views.SessionView;
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
		liveCodingFolder.addPlaceholder(SourceSettingsView.ID);
		liveCodingFolder.addPlaceholder(LiveCodingSettingsView.ID);
		liveCodingFolder.addPlaceholder(CompilerSettingsView.ID);
		liveCodingFolder.addPlaceholder(SessionView.ID + ":*");
		SessionViewManager.init(layout, liveCodingFolder);

		layout.getViewLayout(SourceSettingsView.ID).setCloseable(false);
		layout.getViewLayout(LiveCodingSettingsView.ID).setCloseable(false);
		layout.getViewLayout(CompilerSettingsView.ID).setCloseable(false);
		
		// Messages
    IFolderLayout messagesFolder = layout.createFolder("messages", IPageLayout.LEFT, 0.40f, editorArea);
		messagesFolder.addPlaceholder(MessagesView.ID);
    MessagesManager.init(layout, messagesFolder);
		MessagesManager.getInstance().addTab("Log");
		
		// Console
    IFolderLayout fcshFolder = layout.createFolder("fcsh", IPageLayout.RIGHT, 0.60f, editorArea);
    fcshFolder.addView(FCSHConsoleView.ID);   
    layout.getViewLayout(FCSHConsoleView.ID).setCloseable(false);
    
	}

  public String getName() {
    return name;
  }
	
	
	
}
