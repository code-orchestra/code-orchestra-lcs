package org.casalib.util{
  
  import flash.utils.Dictionary;
  import flash.external.ExternalInterface;
  
  /**
   * 		Utility for providing easy access to the browser query string.
   * 		
   * 		@author Aaron Clinger
   * 		@version 03/28/10
   */
  public class QueryStringUtil extends Object {
    protected static var _query : String ;
    protected static var _hasRequested : Boolean ;
    protected static var _pairMap : Dictionary ;
    /**
     * 			The field/value pairs of the browser URL.
     */
    public static function get queryString (  ) : String {
      if ( !(QueryStringUtil._hasRequested) ) {
        QueryStringUtil._hasRequested = true;
        
        if ( ExternalInterface.available ) {
          try {
            const query : String  = ExternalInterface.call('document.location.search.toString');
            
            if ( query != '' && query != null ) {
              QueryStringUtil._query = query.substring(1);
              
              const pairs : Array  = QueryStringUtil._query.split('&');
              var i : int  = -1;
              var pair : Array;
              
              QueryStringUtil._pairMap = new Dictionary();
              
              while ( ++i < pairs.length ) {
                pair = pairs[i].split('=');
                
                QueryStringUtil._pairMap[pair[0]] = pair[1];
              }
            }
          } catch ( e : Error ) {
            
          }
        }
      }
      
      return QueryStringUtil._query;
    }
    /**
     * 			Returns a query string value by key.
     * 			
     * 			@param key: The key of the query string value to retrieve.
     * 			@return The string value of the key.
     */
    public static function getValue ( key : String ) : String {
      if ( QueryStringUtil.queryString == null ) {
        return null;
        
      }
      
      return QueryStringUtil._pairMap[key];
    }
    /**
     * 			Checks to if query string key exists.
     * 			
     * 			@param key: The name of the key to check for existence.
     * 			@return Returns <code>true</code> if the key exists; otherwise <code>false</code>.
     */
    public static function hasKey ( key : String ) : Boolean {
      return QueryStringUtil.getValue(key) ? true : false;
    }
  }
}




