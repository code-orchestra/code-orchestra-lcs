package org.casalib.util{
  
  import flash.utils.Dictionary;
  
  /**
   * 		Provides utility functions for creating and managing singletons and multitons.
   * 		
   * 		@author Aaron Clinger
   * 		@version 05/04/11
   */
  public class SingletonUtil extends Object {
    protected static var _singletonMap : Dictionary ;
    protected static var _multitonMap : Dictionary ;
    /**
     * 			Creates a singleton out of a class without adapting or extending the class itself.
     * 			
     * 			@param type: The class you want a to created a singleton from.
     * 			@return The singleton instance of the class.
     * 			@example
     * 				<code>
     * 					var stopwatch:Stopwatch = SingletonUtil.singleton(Stopwatch);
     * 					stopwatch.start();
     * 				</code>
     */
    public static function singleton ( type : Class ) : * {
      if ( SingletonUtil._singletonMap == null ) {
        SingletonUtil._singletonMap = new Dictionary();
        
      }
      
      return (type in SingletonUtil._singletonMap) ? SingletonUtil._singletonMap[type] : SingletonUtil._singletonMap[type] = new type();
    }
    /**
     * 			Creates a multiton out of a class without adapting or extending the class itself.
     * 			
     * 			@param type: The class you want a to created a multiton from.
     * 			@param id: An unique name per <code>Class</code> <code>type</code>.
     * 			@return The multiton instance of the class.
     * 			@example
     * 				<code>
     * 					var stopwatch:Stopwatch = SingletonUtil.multiton(Stopwatch, "MyUniqueWatchId");
     * 					stopwatch.start();
     * 				</code>
     */
    public static function multiton ( type : Class, id : String ) : * {
      if ( SingletonUtil._multitonMap == null ) {
        SingletonUtil._multitonMap = new Dictionary();
        
      }
      
      if ( !(type in SingletonUtil._multitonMap) ) {
        SingletonUtil._multitonMap[type] = new Dictionary();
        
      }
      
      if ( !(id in SingletonUtil._multitonMap[type]) ) {
        SingletonUtil._multitonMap[type][id] = new type();
        
      }
      
      return SingletonUtil._multitonMap[type][id];
    }
  }
}




