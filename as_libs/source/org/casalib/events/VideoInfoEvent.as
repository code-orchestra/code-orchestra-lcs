package org.casalib.events{
  
  import flash.events.Event;
  
  /**
   * 		An event dispatched when meta data, or cue point is received from the {@link VideoLoad}.
   * 		
   * 		@author Aaron Clinger
   * 		@version 10/27/08
   */
  public class VideoInfoEvent extends Event {
    public static const CUE_POINT : String  = 'cuePoint' ;
    public static const META_DATA : String  = 'metaData' ;
    protected var _infoObject : Object ;
    /**
     * 			Creates a new VideoInfoEvent.
     * 			
     * 			@param type: The type of event.
     * 			@param bubbles: Determines whether the Event object participates in the bubbling stage of the event flow.
     * 			@param cancelable: Determines whether the Event object can be canceled.
     */
    public function VideoInfoEvent( type : String, bubbles : Boolean  = false, cancelable : Boolean  = false ){
      super(type, bubbles, cancelable);
    }
    /**
     * 			The meta data or cue point info object.
     */
    public function get infoObject (  ) : Object {
      return this._infoObject;
    }
    public function set infoObject ( info : Object ) : void {
      this._infoObject = info;
    }
    /**
     * 			@return A string containing all the properties of the event.
     */
    override public function toString (  ) : String {
      return formatToString('VideoInfoEvent', 'type', 'bubbles', 'cancelable', 'infoObject');
    }
    /**
     * 			@return Duplicates an instance of the event.
     */
    override public function clone (  ) : Event {
      var e : VideoInfoEvent  = new VideoInfoEvent(this.type, this.bubbles, this.cancelable);
      e.infoObject = this.infoObject;
      
      return e;
    }
  }
}




