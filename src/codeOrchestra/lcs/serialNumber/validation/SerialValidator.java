package codeOrchestra.lcs.serialNumber.validation;

import codeOrchestra.utils.StringUtils;

/**
 * @author Alexander ELiseyev
 */
  public final class SerialValidator {

  private static final SerialValidator INSTANCE = new SerialValidator();

  public static SerialValidator getInstance() {
    return INSTANCE;
  }

  private static final String SEPATATOR = "-";
  private static final int SEGMENTS_COUNT = 4;

  private SerialValidator() {
  }

  public boolean isValidSerialNumber(String serialNumber) {
    if (serialNumber == null) {
      return false;
    }

    serialNumber = serialNumber.trim();

    String[] segmentsArray = serialNumber.split(SEPATATOR);
    if (segmentsArray == null || segmentsArray.length != SEGMENTS_COUNT) {
      return false;
    }

    for (int i = 0; i < SEGMENTS_COUNT; i++) {
      if (!isValisSegment(segmentsArray[i], i)) {
        return false;
      }
    }

    return true;
  }

  private static String doMagicSwap(String str) {
    return "" + str.charAt(1) + str.charAt(3) + str.charAt(0) + str.charAt(2);
  }

  private boolean isValisSegment(String segment, int segmentNumber) {
    try {
      if (segmentNumber == 1 || segmentNumber == 3) {
        segment = StringUtils.reverse(segment);
      } else {
        segment = doMagicSwap(segment);
      }
      return PrimeValidator.checkPrime(Radix.fromRadix(segment));
    } catch (Throwable t) {
      return false;
    }
  }

}
