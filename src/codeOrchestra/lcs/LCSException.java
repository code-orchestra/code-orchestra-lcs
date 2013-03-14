package codeOrchestra.lcs;

/**
 * @author Alexander Eliseyev
 */
@SuppressWarnings("serial")
public class LCSException extends Exception {

  public LCSException() {
    super();
  }

  public LCSException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  public LCSException(String arg0) {
    super(arg0);
  }

  public LCSException(Throwable arg0) {
    super(arg0);
  }
  
}
