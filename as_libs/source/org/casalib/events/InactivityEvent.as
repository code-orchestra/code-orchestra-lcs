package org.casalib.events{
  
  import flash.events.Event;
  
  /**
   * 		An event dispatched from {@link Inactivity}.
   * 		
   * 		@author Aaron Clinger
   * 		@version 10/27/08
   */
  public class InactivityEvent extends Event {
    public static const INACTIVE : String  = 'inactive' ;
    public static const ACTIVATED : String  = 'activated' ;
    protected var _milliseconds : uint ;
    /**
     * 			Creates a new InactivityEvent.
     * 			
     * 			@param type: The type of event.
     * 			@param bubbles: Determines whether the Event object participates in the bubbling stage of the event flow.
     * 			@param cancelable: Determines whether the Event object can be canceled.
     */
    public function InactivityEvent( type : String, bubbles : Boolean  = false, cancelable : Boolean  = false ){
      super(type, bubbles, cancelable);
    }
    /**
     * 			The length of time an user has been inactive.
     */
    public function get milliseconds (  ) : uint {
      return this._milliseconds;
    }
    public function set milliseconds ( time : uint ) : void {
      this._milliseconds = time;
    }
    /**
     * 			@return A string containing all the properties of the event.
     */
    override public function toString (  ) : String {
      return formatToString('InactivityEvent', 'type', 'bubbles', 'cancelable', 'milliseconds');
    }
    /**
     * 			@return Duplicates an instance of the event.
     */
    override public function clone (  ) : Event {
      var e : InactivityEvent  = new InactivityEvent(this.type, this.bubbles, this.cancelable);
      e.milliseconds = this.milliseconds;
      
      return e;
    }
  }
}




