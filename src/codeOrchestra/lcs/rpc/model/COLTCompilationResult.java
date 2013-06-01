package codeOrchestra.lcs.rpc.model;

/**
 * @author Alexander Eliseyev
 */
public class COLTCompilationResult {
  
  private boolean success;
  private String[] errorMessages;
  private String[] warningMessages;
  
  public boolean isSuccess() {
    return success;
  }
  public void setSuccess(boolean success) {
    this.success = success;
  }
  public String[] getErrorMessages() {
    return errorMessages;
  }
  public void setErrorMessages(String[] errorMessages) {
    this.errorMessages = errorMessages;
  }
  public String[] getWarningMessages() {
    return warningMessages;
  }
  public void setWarningMessages(String[] warningMessages) {
    this.warningMessages = warningMessages;
  }

}
