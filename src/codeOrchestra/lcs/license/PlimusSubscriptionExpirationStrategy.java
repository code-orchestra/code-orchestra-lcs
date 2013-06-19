package codeOrchestra.lcs.license;

import java.io.IOException;

import codeOrchestra.lcs.license.plimus.PlimusHelper;
import codeOrchestra.lcs.license.plimus.PlimusResponse;
import codeOrchestra.lcs.license.plimus.PlimusResponseStatus;

/**
 * @author Alexander Eliseyev
 */
public class PlimusSubscriptionExpirationStrategy extends AbstractExpirationWithSerialNumberStrategy implements ExpirationStrategy {
  
  @Override
  public int getDaysLeft() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  protected boolean handleValidationResponse(PlimusResponse plimusResponse) {
    if (plimusResponse.getStatus() == PlimusResponseStatus.SUCCESS) {
      return true;
    }
    
    // TODO: show dialogs accoring to status
    
    return false;
  }
  
  boolean checkIfExpiredLocally() {
    // TODO: implement
    return false;
  }

  @Override
  public boolean isTrialOnly() {
    return false;
  }

  @Override
  public boolean allowTrial() {
    return true;
  }

  @Override
  public boolean exitIfExpired() {
    return true;
  }

  @Override
  public void handleExpiration() {
  }

  @Override
  public boolean showTrialExpiredDialog() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void showTrialInProgressDialog() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean isSubscriptionBased() {
    return true;
  }

  @Override
  public boolean hasExpired() {
    try {
      PlimusResponse validationResponse = PlimusHelper.validateKey(CodeOrchestraLicenseManager.getSerialNumber());
      return handleValidationResponse(validationResponse);
    } catch (IOException e) {
      return checkIfExpiredLocally();
    }
  }
  
  @Override
  public int getDaysInUse() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getExpirationPeriod() {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void registerProduct(String serialNumber, PlimusResponse keyRegistrationResponse) {
    // TODO Auto-generated method stub
    // CodeOrchestraLicenseManager.registerProduct(serialNumber);
  }
  
}
