package codeOrchestra.lcs.rpc.security;

import codeOrchestra.lcs.rpc.COLTRemoteTransferableException;

/**
 * @author Alexander Eliseyev
 */
@SuppressWarnings("serial")
public class InvalidAuthTokenException extends COLTRemoteTransferableException {

  public InvalidAuthTokenException() {
    super();
  }

}
