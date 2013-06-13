package codeOrchestra.lcs.license;


import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import codeOrchestra.lcs.macAddress.SystemCheck;
import codeOrchestra.utils.StringUtils;

/**
 * @author Alexander Eliseyev
 */
public class CodeOrchestraLicenseManager {

  private static Preferences preferences = Preferences.userNodeForPackage(CodeOrchestraLicenseManager.class);

  private static final String SERIAL_NUMBER_KEY = "serial-number";
  private static final String LICENSED_TO_KEY = "licensed-to";

  public static void clearLicenseDate() {
    preferences.put(SERIAL_NUMBER_KEY, StringUtils.EMPTY);
    preferences.put(LICENSED_TO_KEY, StringUtils.EMPTY);

    try {
      preferences.sync();
    } catch (BackingStoreException e) {
      throw new RuntimeException("Can't sync license data", e);
    }
  }

  public static void main(String[] args) {
    clearLicenseDate();
  }

  public static String getSerialNumber() {
    return preferences.get(SERIAL_NUMBER_KEY, StringUtils.EMPTY);
  }

  public static boolean noSerialNumberPresent() {
    return StringUtils.isEmpty(getSerialNumber());
  }
  
  public static boolean isLicenseValid() {
    return SystemCheck.getInstance().isValidMACAddress(getSerialNumber());
  }

  public static void registerProduct(String serialNumber) {
    preferences.put(SERIAL_NUMBER_KEY, serialNumber);

    try {
      preferences.sync();
    } catch (BackingStoreException e) {
      throw new RuntimeException("Can't sync license expiry data", e);
    }
  }

}
