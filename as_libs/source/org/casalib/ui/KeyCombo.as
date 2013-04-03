package org.casalib.ui{
  
  import org.casalib.errors.ArrayContentsError;
  import org.casalib.util.ArrayUtil;
  
  /**
   * 		Class for storing keyboard key code combinations.
   * 		
   * 		@author Aaron Clinger
   * 		@version 10/27/08
   */
  public class KeyCombo extends Object {
    protected var _keyCodes : Array ;
    /**
     * 			Creates and defines a KeyCombo.
     * 			
     * 			@param keyCodes: An Array of <code>uint</code> key codes that define a key combination.
     * 			@throws ArrayContentsError if <code>keyCodes</code> Array contains a value not of type <code>uint</code> or if the Array contains less than 2 values.
     */
    public function KeyCombo( keyCodes : Array ){
      if ( keyCodes.length < 2 ) {
        throw new ArrayContentsError();
        
      }
      
      var l : uint  = keyCodes.length;
      while ( l-- ) {
        if ( !(keyCodes[l] is uint) ) {
          throw new ArrayContentsError();
          
        }
        
      }
      
      this._keyCodes = keyCodes.concat();
    }
    /**
     * 			The key codes that compose this KeyCombo.
     */
    public function get keyCodes (  ) : Array {
      return this._keyCodes.concat();
    }
    /**
     * 			Determines if the KeyCombo specified in the <code>keyCombo</code> parameter is equal to this KeyCombo.
     * 			
     * 			@param keyCombo: The KeyCombo class to compare to this class.
     * 			@return Returns <code>true</code> if the two KeyCombo classes contain the same key codes in the same order; otherwise <code>false</code>.
     */
    public function equals ( keyCombo : KeyCombo ) : Boolean {
      if ( keyCombo == this ) {
        return true;
        
      }
      
      return ArrayUtil.equals(this.keyCodes, keyCombo.keyCodes);
    }
  }
}




