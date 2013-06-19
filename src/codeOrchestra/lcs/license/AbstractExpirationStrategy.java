package codeOrchestra.lcs.license;

/**
 * @author Alexander Eliseyev
 */
public abstract class AbstractExpirationStrategy implements ExpirationStrategy {

  protected abstract int getTrialDaysInUse();
  
  protected abstract int getExpirationPeriod();
  
  protected int getDaysLeft() {
    return getExpirationPeriod() - getTrialDaysInUse() + 1;
  }

  @Override
  public boolean hasExpired() {
    return getTrialDaysInUse() > getExpirationPeriod();
  }
  
}
