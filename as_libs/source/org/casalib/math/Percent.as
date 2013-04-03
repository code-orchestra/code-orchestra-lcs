package org.casalib.math{
  
  
  /**
   * 		Creates a standardized way of describing and storing percentages. You can store and receive percentages in two different formats; regular percentage or as an decimal percentage.
   * 		
   * 		If percent is 37.5% a regular percentage would be expressed as <code>37.5</code> while the decimal percentage will be expressed <code>0.375</code>.
   * 		
   * 		@author Aaron Clinger
   * 		@author Mike Creighton
   * 		@version 02/16/09
   */
  public class Percent extends Object {
    protected var _percent : Number ;
    /**
     * 			Creates a new Percent.
     * 			
     * 			@param percentage: Percent formatted at a percentage or an decimal percentage.
     * 			@param isDecimalPercentage: Indicates if the parameter <code>percentage</code> is a decimal percentage <code>true</code>, or regular percentage <code>false</code>.
     */
    public function Percent( percentage : Number  = 0, isDecimalPercentage : Boolean  = true ){
      super();
      
      if ( isDecimalPercentage ) {
        this.decimalPercentage = percentage;
      }else{
        this.percentage = percentage;
      }
    }
    /**
     * 			The percent expressed as a regular percentage. 37.5% would be expressed as <code>37.5</code>.
     */
    public function get percentage (  ) : Number {
      return 100 * this._percent;
    }
    public function set percentage ( percent : Number ) : void {
      this._percent = percent * .01;
    }
    /**
     * 			The percent expressed as a decimal percentage. 37.5% would be expressed as <code>0.375</code>.
     */
    public function get decimalPercentage (  ) : Number {
      return this._percent;
    }
    public function set decimalPercentage ( percent : Number ) : void {
      this._percent = percent;
    }
    /**
     * 			Determines if the percent specified in the <code>percent</code> parameter is equal to this percent object.
     * 			
     * 			@param percent: A Percent object.
     * 			@return Returns <code>true</code> if percents are identical; otherwise <code>false</code>.
     */
    public function equals ( percent : Percent ) : Boolean {
      return this.decimalPercentage == percent.decimalPercentage;
    }
    /**
     * 			@return A new percent object with the same value as this percent.
     */
    public function clone (  ) : Percent {
      return new Percent(this.decimalPercentage);
    }
    public function valueOf (  ) : Number {
      return this.decimalPercentage;
    }
    public function toString (  ) : String {
      return this.decimalPercentage.toString();
    }
  }
}




