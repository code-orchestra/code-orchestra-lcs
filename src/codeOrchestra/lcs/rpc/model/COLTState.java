package codeOrchestra.lcs.rpc.model;

/**
 * @author Alexander Eliseyev
 */
public class COLTState {
  
  private boolean compilationInProgress;
  
  private COLTConnection[] activeConnections;
  
  public boolean isCompilationInProgress() {
    return compilationInProgress;
  }
  public void setCompilationInProgress(boolean compilationInProgress) {
    this.compilationInProgress = compilationInProgress;
  }
  public COLTConnection[] getActiveConnections() {
    return activeConnections;
  }
  public void setActiveConnections(COLTConnection[] activeConnections) {
    this.activeConnections = activeConnections;
  }

}
