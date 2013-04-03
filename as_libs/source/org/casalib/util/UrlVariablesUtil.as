package org.casalib.util{
  
  import flash.net.URLVariables;
  
  /**
   * 		Utilities for manipulating URLVariables.
   * 		
   * 		@author Aaron Clinger
   * 		@version 05/04/11
   */
  public class UrlVariablesUtil extends Object {
    /**
     * 			Sorts the provided <code>URLVariables</code> key/value pairs alphabetically.
     * 			
     * 			@param urlVars: The <code>URLVariables</code> to alphabetize.
     * 			@return Returns the alphabetized key/value pairs as an ampersand (<code>&</code>) delimited <code>String</code>.
     */
    public static function alphabetize ( urlVars : URLVariables ) : String {
      var pairs : Array  = urlVars.toString().split('&');
      pairs.sort(UrlVariablesUtil._sortAlphabetically);
      
      return pairs.join('&');
    }
    protected static function _sortAlphabetically ( a : String, b : String ) : Number {
      if ( a < b ) {
        return -1;
      } else if ( a > b ) {
        return 1;
      }else{
        return 0;
      }
    }
  }
}




