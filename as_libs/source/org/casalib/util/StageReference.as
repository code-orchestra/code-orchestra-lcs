package org.casalib.util{
  
  import flash.utils.Dictionary;
  import flash.display.Stage;
  
  /**
   * 		Stores a reference to Stage for classes that cannot easily access it. This class allows you to store multiple references by ID to different <code>Stage</code>’s which is helpful in an AIR environment.
   * 		
   * 		@author Aaron Clinger
   * 		@version 05/04/11
   * 		@usageNote You must first initialize the class by setting a reference to Stage. See example below:
   * 		@example
   * 			<code>
   * 				package {
   * 					import org.casalib.display.CasaMovieClip;
   * 					import org.casalib.util.StageReference;
   * 					
   * 					
   * 					public class MyExample extends CasaMovieClip {
   * 						
   * 						
   * 						public function MyExample() {
   * 							super();
   * 							
   * 							StageReference.setStage(this.stage);
   * 							
   * 							trace(StageReference.getStage().stageWidth);
   * 						}
   * 					}
   * 				}
   * 			</code>
   */
  public class StageReference extends Object {
    public static const STAGE_DEFAULT : String  = 'stageDefault' ;
    protected static var _stageMap : Dictionary ;
    /**
     * 			Returns a reference to Stage.
     * 			
     * 			@param id: The identifier for the Stage instance.
     * 			@return The Stage instance.
     * 			@throws <code>Error</code> if you try to get a Stage that has not been defined.
     */
    public static function getStage ( id : String  = StageReference.STAGE_DEFAULT ) : Stage {
      if ( !(id in StageReference._getMap()) ) {
        throw new Error('Cannot get Stage ("' + id + '") before it has been set.');
        
      }
      
      return StageReference._getMap()[id];
    }
    /**
     * 			Stores a reference to Stage.
     * 			
     * 			@param stage: The Stage you wish to store.
     * 			@param id: The identifier for the Stage.
     */
    public static function setStage ( stage : Stage, id : String  = StageReference.STAGE_DEFAULT ) : void {
      StageReference._getMap()[id] = stage;
    }
    /**
     * 			Removes a stored reference to a Stage.
     * 			
     * 			@param id: The identifier for the Stage.
     * 			@return Returns <code>true</code> if the Stage reference was found and removed; otherwise <code>false</code>.
     */
    public static function removeStage ( id : String  = StageReference.STAGE_DEFAULT ) : Boolean {
      if ( !(id in StageReference._getMap()) ) {
        return false;
        
      }
      
      StageReference.setStage(null, id);
      
      return true;
    }
    /**
     * 			Finds all the Stage reference IDs.
     * 			
     * 			@return An Array comprised of all the Stage reference identifiers.
     */
    public static function getIds (  ) : Array {
      return ObjectUtil.getKeys(StageReference._getMap());
    }
    /**
     * 			Finds the identifier for a stored Stage reference.
     * 			
     * 			@param stage: The Stage you wish to find the identifier of.
     * 			@return The id for the stored Stage reference or <code>null</code> if it doesn't exist.
     */
    public static function getStageId ( stage : Stage ) : String {
      const map : Dictionary  = StageReference._getMap();
      
      for ( var i : String in map ) {
        if ( map[i] == stage ) {
          return i;
          
        }
        
      }
      
      return null;
    }
    protected static function _getMap (  ) : Dictionary {
      if ( StageReference._stageMap == null ) {
        StageReference._stageMap = new Dictionary();
        
      }
      
      return StageReference._stageMap;
    }
  }
}




