package codeOrchestra.lcs.rpc;

import codeOrchestra.lcs.rpc.model.COLTRemoteProject;

/**
 * @author Alexander Eliseyev
 */
public interface COLTRemoteService {
  
  void createProject(COLTRemoteProject project);
  
  void loadProject(String path);

}
