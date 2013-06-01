package codeOrchestra.lcs.rpc;

import codeOrchestra.lcs.rpc.model.COLTCompilationResult;
import codeOrchestra.lcs.rpc.model.COLTRemoteProject;
import codeOrchestra.lcs.rpc.model.COLTState;
import codeOrchestra.lcs.rpc.security.InvalidShortCodeException;
import codeOrchestra.lcs.rpc.security.TooManyFailedCodeTypeAttemptsException;

/**
 * @author Alexander Eliseyev
 */
public interface COLTRemoteService {
  
  COLTState getState() throws COLTRemoteTransferableException;
 
  COLTCompilationResult runBaseCompilation() throws COLTRemoteTransferableException;
  
  void requestShortCode(String requestor) throws COLTRemoteTransferableException;
  
  String obtainAuthToken(String shortCode) throws TooManyFailedCodeTypeAttemptsException, InvalidShortCodeException;
  
  void createProject(String securityToken, COLTRemoteProject project) throws COLTRemoteTransferableException;
  
  void loadProject(String securityToken, String path) throws COLTRemoteTransferableException;

}
