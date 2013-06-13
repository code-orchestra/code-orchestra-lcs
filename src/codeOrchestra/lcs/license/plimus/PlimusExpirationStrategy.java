package codeOrchestra.lcs.license.plimus;

import codeOrchestra.lcs.license.ExpirationStrategy;

/**
 * @author Alexander Eliseyev
 */
public interface PlimusExpirationStrategy extends ExpirationStrategy {

  boolean handleValidationResponse(PlimusResponse plimusResponse);
  
  boolean checkIfExpiredLocally();
  
}
