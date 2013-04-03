package org.casalib.events{
  
  import org.casalib.math.Percent;
  import flash.events.Event;
  
  /**
   * 		An event dispatched from {@link VideoLoad}.
   * 		
   * 		@author Aaron Clinger
   * 		@version 04/19/09
   */
  public class VideoLoadEvent extends LoadEvent {
    public static const BUFFERED : String  = 'buffered' ;
    public static const PROGRESS : String  = 'progress' ;
    protected var _buffer : Percent  = new Percent() ;
    protected var _millisecondsUntilBuffered : int  = -1 ;
    /**
     * 			Creates a new VideoLoadEvent.
     * 			
     * 			@param type: The type of event.
     * 			@param bubbles: Determines whether the Event object participates in the bubbling stage of the event flow.
     * 			@param cancelable: Determines whether the Event object can be canceled.
     */
    public function VideoLoadEvent( type : String, bubbles : Boolean  = false, cancelable : Boolean  = false ){
      super(type, bubbles, cancelable);
    }
    /**
     * 			The time remaining in milliseconds until the video has completely buffered.
     * 			
     * 			@usageNote {@link VideoLoad} will report <code>-1</code> milliseconds until two seconds of load time has elapsed.
     */
    public function get millisecondsUntilBuffered (  ) : int {
      return this._millisecondsUntilBuffered;
    }
    public function set millisecondsUntilBuffered ( milliseconds : int ) : void {
      this._millisecondsUntilBuffered = milliseconds;
    }
    /**
     * 			The percent the video has buffered.
     * 			
     * 			@usageNote {@link VideoLoad} will report <code>0 </code> percent until two seconds of load time has elapsed.
     */
    public function get buffer (  ) : Percent {
      return this._buffer.clone();
    }
    public function set buffer ( percent : Percent ) : void {
      this._buffer = percent.clone();
    }
    /**
     * 			@return A string containing all the properties of the event.
     */
    override public function toString (  ) : String {
      return this.formatToString('VideoLoadEvent', 'type', 'bubbles', 'cancelable', 'attempts', 'Bps', 'buffer', 'bytesLoaded', 'bytesTotal', 'httpStatus', 'latency', 'millisecondsUntilBuffered', 'progress', 'retries', 'time');
    }
    /**
     * 			@return Duplicates an instance of the event.
     */
    override public function clone (  ) : Event {
      var e : VideoLoadEvent  = new VideoLoadEvent(this.type, this.bubbles, this.cancelable);
      e.attempts = this.attempts;
      e.Bps = this.Bps;
      e.buffer = this.buffer;
      e.bytesLoaded = this.bytesLoaded;
      e.bytesTotal = this.bytesTotal;
      e.httpStatus = this.httpStatus;
      e.latency = this.latency;
      e.millisecondsUntilBuffered = this.millisecondsUntilBuffered;
      e.progress = this.progress;
      e.retries = this.retries;
      e.time = this.time;
      
      return e;
    }
  }
}




