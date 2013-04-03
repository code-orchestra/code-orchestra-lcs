package org.casalib.math{
  
  
  /**
   * 		Creates a standardized way of describing and storing an extent of variation/a value range.
   * 		
   * 		@author Aaron Clinger
   * 		@author Mike Creighton
   * 		@version 03/26/10
   */
  public class Range extends Object {
    protected var _end : Number ;
    protected var _start : Number ;
    /**
     * 			Creates and defines a Range object.
     * 			
     * 			@param start: Beginning value of the range.
     * 			@param end: Ending value of the range.
     * 			@usageNote You are not required to define the range in the contructor you can do it at any point by calling {@link #setRange}.
     */
    public function Range( start : Number, end : Number ){
      super();
      
      this.setRange(start, end);
    }
    /**
     * 			Defines or redefines range.
     * 			
     * 			@param start: Beginning value of the range.
     * 			@param end: Ending value of the range.
     */
    public function setRange ( start : Number, end : Number ) : void {
      this.start = start;
      this.end = end;
    }
    /**
     * 			The start value of the range.
     */
    public function get start (  ) : Number {
      return this._start;
    }
    public function set start ( value : Number ) : void {
      this._start = value;
    }
    /**
     * 			The end value of the range.
     */
    public function get end (  ) : Number {
      return this._end;
    }
    public function set end ( value : Number ) : void {
      this._end = value;
    }
    /**
     * 			The minimum or smallest value of the range.
     */
    public function get min (  ) : Number {
      return Math.min(this.start, this.end);
    }
    /**
     * 			The maximum or largest value of the range.
     */
    public function get max (  ) : Number {
      return Math.max(this.start, this.end);
    }
    /**
     * 			Determines if value is included in the range including the range's start and end values.
     * 			
     * 			@return Returns <code>true</code> if value was included in range; otherwise <code>false</code>.
     */
    public function isWithinRange ( value : Number ) : Boolean {
      return (value <= this.max && value >= this.min);
    }
    /**
     * 			Calculates the percent of the range.
     * 			
     * 			@param percent: A {@link Percent} object.
     * 			@return The value the percent represent of the range.
     */
    public function getValueOfPercent ( percent : Percent ) : Number {
      var min : Number;
      var max : Number;
      var val : Number;
      var per : Percent  = percent.clone();
      
      if ( this.start <= this.end ) {
        min = this.start;
        max = this.end;
      }else{
        per.decimalPercentage = 1 - per.decimalPercentage;
        
        min = this.end;
        max = this.start;
      }
      
      val = Math.abs(max - min) * per.decimalPercentage + min;
      
      return val;
    }
    /**
     * 			Returns the percentage the value represents out of the range.
     * 			
     * 			@param value: An integer.
     * 			@return A Percent object.
     */
    public function getPercentOfValue ( value : Number ) : Percent {
      return new Percent((value - this.min) / (this.max - this.min));
    }
    /**
     * 			Determines if the range specified by the <code>range</code> parameter is equal to this range object.
     * 			
     * 			@param percent: A Range object.
     * 			@return Returns <code>true</code> if ranges are identical; otherwise <code>false</code>.
     */
    public function equals ( range : Range ) : Boolean {
      return this.start == range.start && this.end == range.end;
    }
    /**
     * 			Determines if this range and the range specified by the <code>range</code> parameter overlap.
     * 			
     * 			@param A Range object.
     * 			@return Returns <code>true</code> if this range contains any value of the range specified; otherwise <code>false</code>.
     */
    public function overlaps ( range : Range ) : Boolean {
      if ( this.equals(range) || this.contains(range) || range.contains(this) || this.isWithinRange(range.start) || this.isWithinRange(range.end) ) {
        return true;
        
      }
      
      return false;
    }
    /**
     * 			Determines if this range contains the range specified by the <code>range</code> parameter.
     * 			
     * 			@param A Range object.
     * 			@return Returns <code>true</code> if this range contains all values of the range specified; otherwise <code>false</code>.
     */
    public function contains ( range : Range ) : Boolean {
      return this.start <= range.start && this.end >= range.end;
    }
    /**
     * 			@return A new range object with the same values as this range.
     */
    public function clone (  ) : Range {
      return new Range(this.start, this.end);
    }
  }
}




