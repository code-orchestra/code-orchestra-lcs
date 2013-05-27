package codeOrchestra.lcs.rpc.security;

import codeOrchestra.lcs.rpc.COLTRemoteTransferableException;

/**
 * @author Alexander Eliseyev
 */
@SuppressWarnings("serial")
public class InvalidShortCodeException extends COLTRemoteTransferableException {

  public InvalidShortCodeException() {
    super();
  }

}
