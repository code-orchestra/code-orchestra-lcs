package org.casalib.util{
  
  
  /**
   * 		Utilities for constructing and working with Classes.
   * 		
   * 		@author Aaron Clinger
   * 		@version 02/13/10
   */
  public class ClassUtil extends Object {
    /**
     * 			Dynamically constructs a Class.
     * 			
     * 			@param type: The Class to create.
     * 			@param arguments: Up to ten arguments to the constructor.
     * 			@return Returns the dynamically created instance of the Class specified by <code>type</code> parameter.
     * 			@throws <code>Error</code> if you pass more arguments than this method accepts (accepts ten or less).
     * 			@example
     * 				<code>
     * 					var bData:* = ClassUtil.construct(BitmapData, 200, 200);
     * 					
     * 					trace(bData is BitmapData, bData.width);
     * 				</code>
     */
    public static function construct ( type : Class, ...arguments ) : * {
      if ( arguments.length > 10 ) {
        throw new Error('You have passed more arguments than the "construct" method accepts (accepts ten or less).');
        
      }
      
      switch ( arguments.length ) {
        case  ( 0 ) : {
          return new type();
        }
        case  ( 1 ) : {
          return new type(arguments[0]);
        }
        case  ( 2 ) : {
          return new type(arguments[0], arguments[1]);
        }
        case  ( 3 ) : {
          return new type(arguments[0], arguments[1], arguments[2]);
        }
        case  ( 4 ) : {
          return new type(arguments[0], arguments[1], arguments[2], arguments[3]);
        }
        case  ( 5 ) : {
          return new type(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4]);
        }
        case  ( 6 ) : {
          return new type(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);
        }
        case  ( 7 ) : {
          return new type(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6]);
        }
        case  ( 8 ) : {
          return new type(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7]);
        }
        case  ( 9 ) : {
          return new type(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8]);
        }
        case  ( 10 ) : {
          return new type(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8], arguments[9]);
        }
      }
    }
  }
}




