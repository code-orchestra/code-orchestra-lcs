package org.casalib.events{
  
  import flash.events.Event;
  import org.casalib.math.Percent;
  
  /**
   * 		An event dispatched from {@link Tween}.
   * 		
   * 		@author Mike Creighton
   * 		@author Aaron Clinger
   * 		@version 10/27/08
   */
  public class TweenEvent extends Event {
    public static const COMPLETE : String  = 'complete' ;
    public static const RESUME : String  = 'resume' ;
    public static const START : String  = 'start' ;
    public static const STOP : String  = 'stop' ;
    public static const UPDATE : String  = 'update' ;
    protected var _position : Number ;
    protected var _progress : Percent ;
    /**
     * 			Creates a new TweenEvent.
     * 			
     * 			@param type: The type of event.
     * 			@param bubbles: Determines whether the Event object participates in the bubbling stage of the event flow.
     * 			@param cancelable: Determines whether the Event object can be canceled.
     */
    public function TweenEvent( type : String, bubbles : Boolean  = false, cancelable : Boolean  = false ){
      super(type, bubbles, cancelable);
    }
    /**
     * 			The current position of the tween.
     */
    public function get position (  ) : Number {
      return this._position;
    }
    public function set position ( value : Number ) : void {
      this._position = value;
    }
    /**
     * 			The percent completed of the tween's duration.
     */
    public function get progress (  ) : Percent {
      return this._progress.clone();
    }
    public function set progress ( percent : Percent ) : void {
      this._progress = percent.clone();
    }
    /**
     * 			@return A string containing all the properties of the event.
     */
    override public function toString (  ) : String {
      return formatToString('TweenEvent', 'type', 'bubbles', 'cancelable', 'position', 'progress');
    }
    /**
     * 			@return Duplicates an instance of the event.
     */
    override public function clone (  ) : Event {
      var e : TweenEvent  = new TweenEvent(this.type, this.bubbles, this.cancelable);
      e.position = this.position;
      e.progress = this.progress;
      
      return e;
    }
  }
}




