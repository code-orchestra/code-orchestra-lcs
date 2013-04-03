package org.casalib.util{
  
  import flash.utils.ByteArray;
  
  /**
   * 		Utilities for working with Objects.
   * 		
   * 		@author Aaron Clinger
   * 		@author David Nelson
   * 		@author Rob Gungor
   * 		@version 03/29/10
   */
  public class ObjectUtil extends Object {
    /**
     * 			Searches the first level properties of an object for a another object.
     * 			
     * 			@param obj: Object to search in.
     * 			@param member: Object to search for.
     * 			@return Returns <code>true</code> if object was found; otherwise <code>false</code>.
     */
    public static function contains ( obj : Object, member : Object ) : Boolean {
      for ( var prop : String in obj ) {
        if ( obj[prop] == member ) {
          return true;
          
        }
        
      }
      
      return false;
    }
    /**
     * 			Makes a clone of the original Object.
     * 			
     * 			@param obj: Object to make the clone of.
     * 			@return Returns a duplicate Object.
     * 			@example
     * 				<code>
     * 					this._author      = new Person();
     * 					this._author.name = "Aaron";
     * 					
     * 					registerClassAlias("Person", Person);
     * 					
     * 					var humanClone:Person = Person(ObjectUtil.clone(this._author));
     * 					
     * 					trace(humanClone.name);
     * 				</code>
     */
    public static function clone ( obj : Object ) : Object {
      var byteArray : ByteArray  = new ByteArray();
      byteArray.writeObject(obj);
      byteArray.position = 0;
      
      return byteArray.readObject();
    }
    /**
     * 			Creates an Array comprised of all the keys in an Object.
     * 			
     * 			@param obj: Object in which to find keys.
     * 			@return Array containing all the string key names.
     */
    public static function getKeys ( obj : Object ) : Array {
      var keys : Array  = new Array();
      
      for ( var i : String in obj ) {
        keys.push(i);
        
      }
      
      return keys;
    }
    /**
     * 			Determines if an Object contains a specific method.
     * 			
     * 			@param obj: Object in which to determine a method existence.
     * 			@param methodName: The name of the method.
     * 			@return Returns <code>true</code> if the method exists; otherwise <code>false</code>.
     */
    public static function isMethod ( obj : Object, methodName : String ) : Boolean {
      if ( obj.hasOwnProperty(methodName) ) {
        return obj[methodName] is Function;
        
      }
      
      return false;
    }
    /**
     * 			Uses the strict equality operator to determine if object is <code>undefined</code>.
     * 			
     * 			@param obj: Object to determine if <code>undefined</code>.
     * 			@return Returns <code>true</code> if object is <code>undefined</code>; otherwise <code>false</code>.
     */
    public static function isUndefined ( obj : Object ) : Boolean {
      return obj is undefined;
    }
    /**
     * 			Uses the strict equality operator to determine if object is <code>null</code>.
     * 			
     * 			@param obj: Object to determine if <code>null</code>.
     * 			@return Returns <code>true</code> if object is <code>null</code>; otherwise <code>false</code>.
     */
    public static function isNull ( obj : Object ) : Boolean {
      return obj === null;
    }
    /**
     * 			Determines if object contains no value(s).
     * 			
     * 			@param obj: Object to derimine if empty.
     * 			@return Returns <code>true</code> if object is empty; otherwise <code>false</code>.
     * 			@example
     * 				<code>
     * 					var testNumber:Number;
     * 					var testArray:Array   = new Array();
     * 					var testString:String = "";
     * 					var testObject:Object = new Object();
     * 					
     * 					trace(ObjectUtil.isEmpty(testNumber)); // traces "true"
     * 					trace(ObjectUtil.isEmpty(testArray));  // traces "true"
     * 					trace(ObjectUtil.isEmpty(testString)); // traces "true"
     * 					trace(ObjectUtil.isEmpty(testObject)); // traces "true"
     * 				</code>
     */
    public static function isEmpty ( obj : * ) : Boolean {
      if ( obj == undefined ) {
        return true;
        
      }
      
      if ( obj is Number ) {
        return isNaN(obj);
        
      }
      
      if ( obj is Array || obj is String ) {
        return obj.length == 0;
        
      }
      
      if ( obj is Object ) {
        for ( var prop : String in obj ) {
          return false;
          
        }
        
        return true;
      }
      
      return false;
    }
  }
}




