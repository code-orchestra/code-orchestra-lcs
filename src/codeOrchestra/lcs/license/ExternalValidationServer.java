package codeOrchestra.lcs.license;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author Alexander Eliseyev
 */
public final class ExternalValidationServer {
  
  private static final int PORT = 9037;
  
  private static ExternalValidationServer instance;
  
  public synchronized final static ExternalValidationServer getInstance() {
    if (instance == null) {
      instance = new ExternalValidationServer();
    }
    return instance;
  }
  
  private ExternalValidationService service;
  
  public ExternalValidationServer() {
    this.service = new ExternalValidationServiceImpl();
  }
  
  public void start() {
    // Bind the remote object's stub in the registry
    try {
      ExternalValidationService stub = (ExternalValidationService) UnicastRemoteObject.exportObject(service, 0);
      Registry registry = LocateRegistry.createRegistry(PORT);
      registry.bind("ExternalValidationService", stub);
    } catch (RemoteException e) {
      throw new RuntimeException("Can't export validation server", e);
    } catch (AlreadyBoundException e) {
      throw new RuntimeException("Can't export validation server", e);
    }
  }

}
