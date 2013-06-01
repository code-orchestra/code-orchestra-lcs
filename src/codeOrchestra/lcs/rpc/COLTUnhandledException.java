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
    super(message, cause);
  }

  public COLTUnhandledException(String message) {
    super(message);
  }

  public COLTUnhandledException(Throwable cause) {
    super(cause);
  } 

}
