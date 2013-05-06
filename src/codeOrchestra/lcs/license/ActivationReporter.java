package codeOrchestra.lcs.license;

import java.net.NetworkInterface;
import java.util.Enumeration;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

public class ActivationReporter {

  private static final String ACTIVATION_URL = "http://activation.codeorchestra.com";

  private String serialNumber;
  
  private HttpClient httpClient = new HttpClient();

  public ActivationReporter(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  public void report() {
    try {
      PostMethod postMethod = new PostMethod(ACTIVATION_URL);
      postMethod.setParameter("sn", serialNumber);
      postMethod.setParameter("fp", getFingerPrint());
      httpClient.executeMethod(postMethod);
      
      System.out.println("Activation: " + postMethod.getStatusCode());
    } catch (Throwable t) {
      // ignore
      t.printStackTrace();
    }
  }

  private static String getFingerPrint() {
    StringBuilder sb = new StringBuilder();
    try {
      for (final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements();) {
        final NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
        if (networkInterface.isLoopback()) {
          continue;
        }
        
        byte[] mac = networkInterface.getHardwareAddress();

        for (int i = 0; i < mac.length; i++) {
          sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));    
        }
        
        if (interfaces.hasMoreElements()) {
          sb.append("|");
        }
      }
    } catch (Exception e) {
      // ignore
    }
    
    String result = sb.toString();
    if (result.endsWith("|")) {
      return result.substring(0, result.length() - 1);
    }

    return result;
  }

}
