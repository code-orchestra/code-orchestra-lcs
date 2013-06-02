package codeOrchestra.lcs.rpc;

/**
 * @author Alexander Eliseyev
 */
@SuppressWarnings("serial")
public class COLTUnhandledException extends COLTRemoteTransferableException {

  public COLTUnhandledException() {
    super();
  }

  public COLTUnhandledException(String message, Throwable cause) {
    super(message + ", caused by: " + getCauseMessage(cause));
  }

  public COLTUnhandledException(String message) {
    super(message);
  }

  public COLTUnhandledException(Throwable cause) {
    super(getCauseMessage(cause));
  }

  private static String getCauseMessage(Throwable cause) {
    return cause.getClass().getSimpleName() + (cause.getMessage() != null ? ": " + cause.getMessage() : "");
  } 
  
}
