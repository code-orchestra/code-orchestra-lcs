package codeOrchestra.lcs;

/**
 * Interface defining the application's command IDs.
 * Key bindings can be defined for specific commands.
 * To associate an action with a command, use IAction.setActionDefinitionId(commandId).
 *
 * @see org.eclipse.jface.action.IAction#setActionDefinitionId(String)
 */
public interface ICommandIds {

    public static final String CMD_NEW = "LCS.new";
    public static final String CMD_OPEN = "LCS.open";
	public static final String CMD_SAVE_ALL = "LCS.saveAll";
    
}
