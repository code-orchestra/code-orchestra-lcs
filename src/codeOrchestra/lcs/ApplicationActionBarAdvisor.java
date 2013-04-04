package codeOrchestra.lcs;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import codeOrchestra.lcs.actions.NewProjectAction;
import codeOrchestra.lcs.actions.OpenProjectAction;
import codeOrchestra.lcs.actions.SaveProjectAction;
import codeOrchestra.lcs.actions.StartSessionAction;
import codeOrchestra.lcs.status.SessionStatusLineItem;

/**
 * @author Alexander Eliseyev
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    // Actions - important to allocate these only in makeActions, and then use them
    // in the fill methods.  This ensures that the actions aren't recreated
    // when fillActionBars is called with FILL_PROXY.
    private IWorkbenchAction exitAction;
    private IWorkbenchAction aboutAction;
    
    private OpenProjectAction openProjectAction;
    private SaveProjectAction saveProjectAction;    
    private NewProjectAction newProjectAction;
    
    private StartSessionAction startSessionAction;

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }
    
    protected void makeActions(final IWorkbenchWindow window) {
        // Creates the actions and registers them.
        // Registering is needed to ensure that key bindings work.
        // The corresponding commands keybindings are defined in the plugin.xml file.
        // Registering also provides automatic disposal of the actions when
        // the window is closed.

        exitAction = ActionFactory.QUIT.create(window);
        exitAction.setImageDescriptor(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/close.gif"));
        register(exitAction);
        
        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);

        newProjectAction = new NewProjectAction(window);
        register(newProjectAction);
        
        saveProjectAction = new SaveProjectAction(window, "Save Project");
        register(saveProjectAction);
        
        openProjectAction = new OpenProjectAction(window, "Open Project");
        register(openProjectAction);
        
        startSessionAction = new StartSessionAction(window);
        register(startSessionAction);
    }
    
    protected void fillMenuBar(IMenuManager menuBar) {
        MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
        MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
        
        menuBar.add(fileMenu);
        // Add a group marker indicating where action set menus will appear.
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        menuBar.add(helpMenu);
        
        // File
        fileMenu.add(newProjectAction);
        fileMenu.add(new Separator());
        fileMenu.add(openProjectAction);
        fileMenu.add(new Separator());
        fileMenu.add(saveProjectAction);
        fileMenu.add(new Separator());        
        fileMenu.add(exitAction);
        
        // Help
        helpMenu.add(aboutAction);
    }
    
    protected void fillCoolBar(ICoolBarManager coolBar) {
        IToolBarManager projectToolBar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);          
        projectToolBar.add(newProjectAction);
        projectToolBar.add(openProjectAction);
        projectToolBar.add(saveProjectAction);        
        projectToolBar.add(exitAction);
        coolBar.add(new ToolBarContributionItem(projectToolBar, "project"));
                
        IToolBarManager runToolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
        runToolbar.add(startSessionAction);
        coolBar.add(new ToolBarContributionItem(runToolbar, "run"));
    }
    
    
    @Override
    protected void fillStatusLine(IStatusLineManager statusLine) {
      SessionStatusLineItem sessionStatusLineItem = new SessionStatusLineItem();
      statusLine.add(sessionStatusLineItem);
    }
    
}
