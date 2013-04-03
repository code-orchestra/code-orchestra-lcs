package org.casalib.events{
  
  import flash.events.Event;
  
  /**
   * 		An event dispatched during a {@link Process}.
   * 		
   * 		@author Aaron Clinger
   * 		@version 02/01/10
   */
  public class ProcessEvent extends Event {
    public static const COMPLETE : String  = 'processComplete' ;
    public static const START : String  = 'processStart' ;
    public static const STOP : String  = 'processStop' ;
    /**
     * 			Creates a new ProcessEvent.
     * 			
     * 			@param type: The type of event.
     * 			@param bubbles: Determines whether the Event object participates in the bubbling stage of the event flow.
     * 			@param cancelable: Determines whether the Event object can be canceled.
     */
    public function ProcessEvent( type : String, bubbles : Boolean  = false, cancelable : Boolean  = false ){
      super(type, bubbles, cancelable);
    }
    /**
     * 			@return A string containing all the properties of the event.
     */
    override public function toString (  ) : String {
      return formatToString('ProcessEvent', 'type', 'bubbles', 'cancelable');
    }
    /**
     * 			@return Duplicates an instance of the event.
     */
    override public function clone (  ) : Event {
      var e : ProcessEvent  = new ProcessEvent(this.type, this.bubbles, this.cancelable);
      
      return e;
    }
  }
}




