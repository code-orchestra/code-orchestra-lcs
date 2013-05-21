package codeOrchestra.lcs.rpc;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeOrchestra.lcs.rpc.impl.COLTRemoteServiceImpl;

import com.googlecode.jsonrpc4j.JsonRpcServer;

/**
 * @author Alexander Eliseyev
 */
@SuppressWarnings("serial")
public class COLTRemoteServiceServlet extends HttpServlet {
  
  private COLTRemoteService coltRemoteService;
  private JsonRpcServer jsonRpcServer;
  
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doPost(req, resp);
  }
  
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
    try {
      jsonRpcServer.handle(req, resp);
    } catch (IOException e) {
      // TODO: handle
      e.printStackTrace();
    }
  }

  public void init(ServletConfig config) {
    this.coltRemoteService = COLTRemoteServiceImpl.getInstance();
    this.jsonRpcServer = new JsonRpcServer(this.coltRemoteService, COLTRemoteService.class);
  }

}
