package codeOrchestra.lcs.license;

/**
 * @author Alexander Eliseyev
 */
public abstract class AbstractExpirationStrategy implements ExpirationStrategy {

  @Override
  public int getDaysLeft() {
    return getExpirationPeriod() - getDaysInUse() + 1;
  }

  @Override
  public boolean hasExpired() {
    return getDaysInUse() > getExpirationPeriod();
  }
  
}
