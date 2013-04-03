package org.casalib.util{
  
  
  /**
   * 		Utility for providing easy access to HTML embeded FlashVars.
   * 		
   * 		@author Aaron Clinger
   * 		@version 09/15/08
   * 		@usageNote You must first initialize {@link StageReference} before using this class.
   */
  public class FlashVarUtil extends Object {
    /**
     * 			Returns a FlashVar value by key.
     * 			
     * 			@param key: The name of the FlashVar to retrieve.
     * 			@return The string value of the FlashVar.
     * 			@usageNote You must first initialize {@link StageReference} before using this class.
     */
    public static function getValue ( key : String ) : String {
      return StageReference.getStage().loaderInfo.parameters[key];
    }
    /**
     * 			Checks to if FlashVar exists.
     * 			
     * 			@param key: The name of the FlashVar to check for existence.
     * 			@return Returns <code>true</code> if the key exists; otherwise <code>false</code>.
     * 			@usageNote You must first initialize {@link StageReference} before using this class.
     */
    public static function hasKey ( key : String ) : Boolean {
      return FlashVarUtil.getValue(key) ? true : false;
    }
  }
}




