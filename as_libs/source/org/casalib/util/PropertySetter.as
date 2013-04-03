package org.casalib.util{
  
  
  /**
   * 		Creates a setter function for properties. Designed to be used with objects where methods require a function but you want to ultimately set a value of a property.
   * 		
   * 		@author Aaron Clinger
   * 		@author Mike Creighton
   * 		@author David Nelson
   * 		@version 02/13/10
   */
  public class PropertySetter extends Object {
    protected var _scope : Object ;
    protected var _property : String ;
    protected var _argument : int ;
    /**
     * 			Defines the property you wish to define with {@link #defineProperty}.
     * 			
     * 			@param scope: An object that contains the property specified by <code>property</code>.
     * 			@param property: Name of the property you want to assign the value of.
     * 			@param argument: The position the value to assign falls in the argument order.
     * 			@throws <code>Error</code> if the property does not exist or is not available in defined scope.
     */
    public function PropertySetter( scope : Object, property : String, argument : uint  = 0 ){
      if ( scope[property] == undefined ) {
        throw new Error('Property "' + property + '" does not exist or is not available in defined scope.');
        
      }
      
      this._scope = scope;
      this._property = property;
      this._argument = argument;
    }
    /**
     * 			Defines property with the value of the targeted argument.
     */
    public function defineProperty ( ...arguments ) : void {
      this._scope[this._property] = arguments[this._argument];
    }
  }
}




