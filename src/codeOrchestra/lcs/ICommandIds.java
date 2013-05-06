package codeOrchestra.lcs;

/**
 * @author Alexander Eliseyev
 */
public interface ICommandIds {

    public static final String CMD_NEW_PROJECT = "LCS.newProject";
  
    public static final String CMD_OPEN = "LCS.open";
    public static final String CMD_SAVE = "LCS.save";
    
    public static final String CMD_START_SESSION = "LCS.sessionStart";
    public static final String CMD_START_PLUS = "LCS.startPlus";
    
    public static final String CMD_CLEAR_CACHES = "LCS.clearCaches";
    
    public static final String CMD_GO_TO_LOGS_FOLDER = "LCS.goToLogsFolder";

    public static final String CMD_ENTER_SERIAL = "LCS.enterSerial";
    
}
