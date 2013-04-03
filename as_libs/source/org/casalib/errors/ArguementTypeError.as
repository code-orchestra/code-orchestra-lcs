package org.casalib.errors{
  
  
  /**
   * 		@author Aaron Clinger
   * 		@version 08/06/08
   */
  public class ArguementTypeError extends ArgumentError {
    /**
     * 			Creates a new ArguementTypeError.
     * 			
     * 			@param paramName: The name of the parameter with the incorrect type.
     */
    public function ArguementTypeError( paramName : String  = null ){
      super((paramName == null) ? 'You passed an argument with an incorrect type to this method.' : 'The argument type you passed for parameter "' + paramName + '" is not allowed by this method.');
    }
  }
}




