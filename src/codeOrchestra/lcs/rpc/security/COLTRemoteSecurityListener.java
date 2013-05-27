package codeOrchestra.lcs.rpc.security;

/**
 * @author Alexander Eliseyev
 */
public interface COLTRemoteSecurityListener {

  void onNewRequest(String requestor, String shortCode);
  
  void onSuccessfulActivation(String shortCode);
  
}
