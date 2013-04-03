package org.casalib.events{
  
  import flash.events.Event;
  
  /**
   * 		An event dispatched when a load request is retried after previously failing.
   * 		
   * 		@author Aaron Clinger
   * 		@version 10/27/08
   */
  public class RetryEvent extends Event {
    public static const RETRY : String  = 'retry' ;
    protected var _attempts : int ;
    /**
     * 			Creates a new LoadEvent.
     * 			
     * 			@param type: The type of event.
     * 			@param bubbles: Determines whether the Event object participates in the bubbling stage of the event flow.
     * 			@param cancelable: Determines whether the Event object can be canceled.
     */
    public function RetryEvent( type : String, bubbles : Boolean  = false, cancelable : Boolean  = false ){
      super(type, bubbles, cancelable);
    }
    /**
     * 			The number of times the file has attempted to load.
     */
    public function get attempts (  ) : int {
      return this._attempts;
    }
    public function set attempts ( amount : int ) : void {
      this._attempts = amount;
    }
    /**
     * 			@return A string containing all the properties of the event.
     */
    override public function toString (  ) : String {
      return formatToString('RetryEvent', 'type', 'bubbles', 'cancelable', 'attempts');
    }
    /**
     * 			@return Duplicates an instance of the event.
     */
    override public function clone (  ) : Event {
      var e : RetryEvent  = new RetryEvent(this.type, this.bubbles, this.cancelable);
      e.attempts = this.attempts;
      
      return e;
    }
  }
}




