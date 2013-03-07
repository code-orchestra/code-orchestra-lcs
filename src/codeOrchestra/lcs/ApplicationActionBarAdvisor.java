package codeOrchestra.lcs;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
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

import codeOrchestra.lcs.actions.NewLiveCodingConfigurationAction;
import codeOrchestra.lcs.actions.OpenLiveCodingConfigurationAction;
import codeOrchestra.lcs.actions.SaveAllAction;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of the
 * actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    // Actions - important to allocate these only in makeActions, and then use them
    // in the fill methods.  This ensures that the actions aren't recreated
    // when fillActionBars is called with FILL_PROXY.
    private IWorkbenchAction exitAction;
    private IWorkbenchAction aboutAction;
    private NewLiveCodingConfigurationAction newViewAction;
    private OpenLiveCodingConfigurationAction openViewAction;
    private SaveAllAction saveAllAction;

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
        
        newViewAction = new NewLiveCodingConfigurationAction(window, "Create New Live Configuration", LiveConfigurationView.ID);
        register(newViewAction);
        
        saveAllAction = new SaveAllAction(window, "Sace all Live Configurations", LiveConfigurationView.ID);
        register(saveAllAction);
        
        openViewAction = new OpenLiveCodingConfigurationAction(window, "Open Existing Live Configuration", LiveConfigurationView.ID);
        register(openViewAction);
    }
    
    protected void fillMenuBar(IMenuManager menuBar) {
        MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
        MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
        
        menuBar.add(fileMenu);
        // Add a group marker indicating where action set menus will appear.
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        menuBar.add(helpMenu);
        
        // File
        fileMenu.add(newViewAction);
        fileMenu.add(new Separator());
        fileMenu.add(openViewAction);
        fileMenu.add(new Separator());
        fileMenu.add(saveAllAction);
        fileMenu.add(new Separator());        
        fileMenu.add(exitAction);
        
        // Help
        helpMenu.add(aboutAction);
    }
    
    protected void fillCoolBar(ICoolBarManager coolBar) {
        IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
        coolBar.add(new ToolBarContributionItem(toolbar, "main"));   
        toolbar.add(newViewAction);
        toolbar.add(openViewAction);
        toolbar.add(saveAllAction);        
        toolbar.add(exitAction);
    }
}
