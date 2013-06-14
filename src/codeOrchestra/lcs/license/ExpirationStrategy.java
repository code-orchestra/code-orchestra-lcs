package codeOrchestra.lcs.license;

/**
 * @author Alexander Eliseyev
 */
public interface ExpirationStrategy {

  boolean isTrialOnly();
  
  boolean allowTrial();
  
  boolean hasExpired();  
  
  
  int getDaysInUse();
  
  int getDaysLeft();
  
  int getExpirationPeriod();

  
  boolean exitIfExpired();

  void handleExpiration();

  
  /**
   * @return whether the user entered a serial number in the dialog
   */
  boolean showTrialExpiredDialog();

  void showTrialInProgressDialog();

  boolean isSubscriptionBased();
  
  boolean showSerialNumberDialog();

}
