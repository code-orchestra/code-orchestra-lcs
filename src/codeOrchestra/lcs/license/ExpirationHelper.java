package codeOrchestra.lcs.license;

import java.util.Date;

public class ExpirationHelper {

  private static final Date EXPIRATION_DATE = new Date(2013 - 1900, 4, 15);
  
  private static boolean canExpire = true;
  
  public static boolean hasExpired() {
    if (!canExpire) {
      return false;
    }
    return new Date().after(EXPIRATION_DATE);
  }
  
  public static String getExpirationMessage() {
    return canExpire ?  "Your copy of COLT will expire on " + EXPIRATION_DATE.toLocaleString() : "";
  }
  
}
