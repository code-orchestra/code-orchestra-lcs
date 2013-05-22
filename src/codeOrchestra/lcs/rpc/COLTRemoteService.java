package codeOrchestra.lcs.rpc;

import codeOrchestra.lcs.rpc.model.COLTRemoteProject;

/**
 * @author Alexander Eliseyev
 */
public interface COLTRemoteService {
  
  void createProject(String securityToken, COLTRemoteProject project);
  
  void loadProject(String securityToken, String path);

}
