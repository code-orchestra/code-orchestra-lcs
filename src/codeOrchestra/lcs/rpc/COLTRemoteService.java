package codeOrchestra.lcs.rpc;

import codeOrchestra.lcs.rpc.model.COLTRemoteProject;
import codeOrchestra.lcs.rpc.security.InvalidShortCodeException;
import codeOrchestra.lcs.rpc.security.TooManyFailedCodeTypeAttemptsException;

/**
 * @author Alexander Eliseyev
 */
public interface COLTRemoteService {
 
  void requestShortCode(String requestor) throws COLTRemoteTransferableException;
  
  String obtainAuthToken(String shortCode) throws TooManyFailedCodeTypeAttemptsException, InvalidShortCodeException;
  
  void createProject(String securityToken, COLTRemoteProject project) throws COLTRemoteTransferableException;
  
  void loadProject(String securityToken, String path) throws COLTRemoteTransferableException;

}
