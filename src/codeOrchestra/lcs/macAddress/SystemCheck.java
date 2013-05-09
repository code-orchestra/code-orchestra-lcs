package codeOrchestra.lcs.macAddress;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Alexander ELiseyev
 */
public final class SystemCheck {

  private static final SystemCheck INSTANCE = new SystemCheck();

  public static SystemCheck getInstance() {
    return INSTANCE;
  }

  private static final String SEPATATOR = "-";
  private static final int SEGMENTS_COUNT = 4;

  private static boolean validateByDigest(String macAddress) {
    try {
      String digest = md5(macAddress);
      
      InputStream digestsStream = SystemCheck.class.getResourceAsStream("digests.txt");    
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(digestsStream));

      String line = null;
      while ((line = bufferedReader.readLine()) != null) {
        if (digest.equals(line.trim())) {
          return true;
        }
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
    
    return false;
  }
  
  private SystemCheck() {
  }

  public boolean isValidMACAddress(String macAddress) {
    if (macAddress == null) {
      return false;
    }

    macAddress = macAddress.trim();

    String[] segmentsArray = macAddress.split(SEPATATOR);
    if (segmentsArray == null || segmentsArray.length != SEGMENTS_COUNT) {
      return false;
    }

    for (int i = 0; i < SEGMENTS_COUNT; i++) {
      if (!isValisSegment(segmentsArray[i], i)) {
        return false;
      }
    }

    return validateByDigest(macAddress);
  }

  private static String doMagicSwap(String str) {
    return "" + str.charAt(1) + str.charAt(3) + str.charAt(0) + str.charAt(2);
  }

  private boolean isValisSegment(String segment, int segmentNumber) {
    try {
      if (segmentNumber == 1 || segmentNumber == 3) {
        segment = reverse(segment);
      } else {
        segment = doMagicSwap(segment);
      }
      return PrimeValidator.checkPrime(Radix.fromRadix(segment));
    } catch (Throwable t) {
      return false;
    }
  }
  
  private static String reverse(String str) {
    if (str == null) {
        return null;
    }
    return new StringBuffer(str).reverse().toString();
  }
  
  private static String md5 (String buffer) {
    MessageDigest md5 = null;
    try {
      md5 = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error while digesting", e);
    }
    md5.update(buffer.getBytes());
    byte [] code = md5.digest("COLTSerial".getBytes());
    BigInteger bi = new BigInteger(code).abs();
    return bi.abs().toString(16);
  }

}
