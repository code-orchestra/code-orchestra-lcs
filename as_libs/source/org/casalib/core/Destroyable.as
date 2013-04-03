package org.casalib.core{
  
  
  /**
   * 		Base class for objects that are destroyable.
   * 		
   * 		@author Aaron Clinger
   * 		@version 10/27/08
   */
  public class Destroyable extends Object implements IDestroyable {
    protected var _isDestroyed : Boolean ;
    /**
     * 			Creates a new Destroyable object.
     */
    public function Destroyable(  ){
      super();
    }
    public function get destroyed (  ) : Boolean {
      return this._isDestroyed;
    }
    public function destroy (  ) : void {
      this._isDestroyed = true;
    }
  }
}




