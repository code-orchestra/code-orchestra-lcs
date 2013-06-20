package codeOrchestra.lcs.license.plimus;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * @author Alexander Eliseyev
 */
public final class PlimusHelper {

  private static final int TIMEOUT = 1000;

//  private static final String VALIDATION_URL = "https://www.plimus.com/jsp/validateKey.jsp";
  private static final String VALIDATION_URL = "https://sandbox.plimus.com/jsp/validateKey.jsp"; // TODO: this is a sandbox URL
  
//  private static final String PRODUCT_ID = "902584";
  private static final String PRODUCT_ID = "294006"; // TODO: this is a sandbox product id
  
  private static HttpClient httpClient = new HttpClient();

  private PlimusHelper() {
  }

  public static PlimusResponse registerKey(String key) throws IOException {
    return executePlimusAction(key, PlimusValidationAction.REGISTER);
  }

  public static PlimusResponse validateKey(String key) throws IOException {
    return executePlimusAction(key, PlimusValidationAction.VALIDATE);
  }
  
  private static PlimusResponse executePlimusAction(String key, PlimusValidationAction action) throws IOException {
    GetMethod getMethod = new GetMethod(VALIDATION_URL);

    getMethod.getParams().setParameter("http.socket.timeout", new Integer(TIMEOUT));
    
    getMethod.setQueryString(new NameValuePair[] {
        new NameValuePair("action", action.name()),
        new NameValuePair("productId", PRODUCT_ID),
        new NameValuePair("key", key)
    });

    httpClient.executeMethod(getMethod);
    
    return new PlimusResponse(getMethod.getResponseBodyAsString());
  }

}
