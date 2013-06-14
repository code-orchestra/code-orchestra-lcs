package codeOrchestra.lcs.license;

import java.util.prefs.Preferences;

/**
 * @author Alexander Eliseyev
 */
public class FastCompilerBetaExpirationStrategy extends AbstractExpirationStrategy implements ExpirationStrategy {

  private static final Preferences preferences = Preferences.userNodeForPackage(FastCompilerBetaExpirationStrategy.class);
  
  @Override
  public boolean isTrialOnly() {
    return true;
  }

  @Override
  public boolean allowTrial() {
    return true;
  }

  @Override
  public boolean showTrialExpiredDialog() {
    // TODO: implement
    return false;
  }

  @Override
  public void showTrialInProgressDialog() {
    // TODO: implement    
  }
  
  @Override
  public int getDaysInUse() {
    // TODO: implement
    return 0;
  }

  @Override
  public int getExpirationPeriod() {
    return 10;
  }

  @Override
  public boolean exitIfExpired() {
    return true;
  }

  @Override
  public void handleExpiration() {
  }

  @Override
  public boolean isSubscriptionBased() {
    return false;
  }

  @Override
  public boolean showSerialNumberDialog() {
    throw new UnsupportedOperationException();
  }

}
