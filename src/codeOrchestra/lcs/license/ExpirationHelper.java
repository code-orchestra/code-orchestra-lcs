package codeOrchestra.lcs.license;

/**
 * @author Alexander Eliseyev
 */
public final class ExpirationHelper {

  public static ExpirationStrategy getExpirationStrategy() {
    return expirationStrategy;
  }

  private static final ExpirationStrategy expirationStrategy = new CalendarUsageDayExpirationStrategy();
}
