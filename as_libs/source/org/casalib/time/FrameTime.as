package org.casalib.time{
  
  import flash.events.Event;
  import flash.utils.getTimer;
  
  /**
   * 		Creates a common time which isn't affected by delays caused by code execution; the time is only updated every frame.
   * 		
   * 		@author Aaron Clinger
   * 		@author Mike Creighton
   * 		@version 03/08/08
   */
  public class FrameTime extends Object {
    protected static var _frameTimeInstance : FrameTime ;
    protected var _enterFrame : EnterFrame ;
    protected var _time : int ;
    /**
     * 			@exclude
     */
    public function FrameTime( singletonEnforcer : SingletonEnforcer ){
      this._enterFrame = EnterFrame.getInstance();
      this._enterFrame.addEventListener(Event.ENTER_FRAME, this._updateTime);
      
      this._updateTime(new Event(Event.ENTER_FRAME));
    }
    /**
     * 			@return {@link FrameTime} instance.
     */
    public static function getInstance (  ) : FrameTime {
      if ( FrameTime._frameTimeInstance == null ) {
        FrameTime._frameTimeInstance = new FrameTime(new SingletonEnforcer());
        
      }
      
      return FrameTime._frameTimeInstance;
    }
    /**
     * 			@return Returns the number of milliseconds from when the SWF started playing to the last <code>enterFrame</code> event.
     */
    public function get time (  ) : int {
      return this._time;
    }
    protected function _updateTime ( e : Event ) : void {
      this._time = getTimer();
    }
  }
}




class SingletonEnforcer extends Object {
  public function SingletonEnforcer(  ){
    
  }
}
