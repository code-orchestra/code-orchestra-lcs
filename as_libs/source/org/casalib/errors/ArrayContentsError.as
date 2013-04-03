package org.casalib.errors{
  
  
  /**
   * 		@author Aaron Clinger
   * 		@version 04/13/08
   */
  public class ArrayContentsError extends Error {
    /**
     * 			Creates a new ArrayContentsError.
     */
    public function ArrayContentsError(  ){
      super('The Array is composed of an incorrect number of values, or values of an incorrect type.');
    }
  }
}




