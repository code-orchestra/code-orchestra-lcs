package org.casalib.core{
  
  
  /**
   * 		Interface for objects that are destroyable.
   * 		
   * 		@author Aaron Clinger
   * 		@version 03/16/09
   */
  public interface IDestroyable {
    /**
     * 			Removes any event listeners and stops all internal processes to help allow for prompt garbage collection.
     * 			
     * 			<strong>Always call <code>destroy()</code> before deleting last object pointer.</strong>
     */
    function destroy (  ) : void ;
    /**
     * 			Determines if the object has been destroyed <code>true</code>, or is still available for use <code>false</code>.
     */
    function get destroyed (  ) : Boolean ;
  }
}




