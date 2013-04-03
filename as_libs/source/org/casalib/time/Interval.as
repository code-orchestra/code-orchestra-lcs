package org.casalib.time{
  
  import org.casalib.events.ListenerManager;
  import flash.events.TimerEvent;
  import flash.utils.Timer;
  import org.casalib.core.IDestroyable;
  import org.casalib.events.IRemovableEventDispatcher;
  import org.casalib.control.IRunnable;
  import flash.events.Event;
  
  /**
   * 		To be used instead of <code>flash.utils.setInterval</code> and <code>flash.utils.setTimeout</code> functions.
   * 		
   * 		Advantages over <code>setInterval</code>/<code>setTimeout</code>:
   * 		<ul>
   * 			<li>Ability to stop and start intervals without redefining.</li>
   * 			<li>Change the time (<code>delay</code>), {@link Interval#callBack call back} and {@link Interval#arguments arguments} without redefining.</li>
   * 			<li>Included <code>repeatCount</code> for intervals that only need to fire finitely.</li>
   * 			<li>{@link Interval#setInterval} and {@link Interval#setTimeout} return an object instead of interval id for better OOP structure.</li>
   * 			<li>Built in events/event dispatcher.</li>
   * 		</ul>
   * 		
   * 		@author Aaron Clinger
   * 		@author Mike Creighton
   * 		@version 02/11/10
   * 		@example
   * 			<code>
   * 				package {
   * 					import org.casalib.display.CasaMovieClip;
   * 					import org.casalib.time.Interval;
   * 					
   * 					
   * 					public class MyExample extends CasaMovieClip {
   * 						protected var _interval:Interval;
   * 						
   * 						
   * 						public function MyExample() {
   * 							super();
   * 							
   * 							this._interval = Interval.setInterval(this._repeatingFunction, 1000, "CASA");
   * 							this._interval.repeatCount = 3;
   * 							this._interval.start();
   * 						}
   * 						
   * 						protected function _repeatingFunction(name:String):void {
   * 							trace(name);
   * 						}
   * 					}
   * 				}
   * 			</code>
   */
  public class Interval extends Timer implements IDestroyable, IRemovableEventDispatcher, IRunnable {
    protected var _callBack : Function ;
    protected var _arguments : Array ;
    protected var _isDestroyed : Boolean ;
    protected var _listenerManager : ListenerManager ;
    /**
     * 			@exclude
     */
    public function Interval( delay : Number, repeatCount : int, callBack : Function, args : Array ){
      super(delay, repeatCount);
      
      this.callBack = callBack;
      this.arguments = args;
      this._listenerManager = ListenerManager.getManager(this);
      
      super.addEventListener(TimerEvent.TIMER, this._timerHandler, false, 0, true);
    }
    /**
     * 			Runs a function at a specified periodic interval.
     * 			
     * 			@param callBack: The function to execute after specified delay.
     * 			@param delay: The time in milliseconds between calls.
     * 			@param arguments: The arguments to be passed to the call back function when executed.
     * 			@return: An {@link Interval} reference.
     */
    public static function setInterval ( callBack : Function, delay : Number, ...arguments ) : Interval {
      return new Interval(delay, 0, callBack, arguments);
    }
    /**
     * 			Runs a function at a specified periodic interval. Acts identically like {@link Interval#setInterval} except <code>setTimeout</code> defaults <code>repeatCount</code> to <code>1</code>.
     * 			
     * 			@param callBack: The function to execute after specified delay.
     * 			@param delay: The time in milliseconds between calls.
     * 			@param arguments: The arguments to be passed to the call back function when executed.
     * 			@return: An {@link Interval} reference.
     */
    public static function setTimeout ( callBack : Function, delay : Number, ...arguments ) : Interval {
      return new Interval(delay, 1, callBack, arguments);
    }
    /**
     * 			The function to execute after specified delay.
     */
    public function get callBack (  ) : Function {
      return this._callBack;
    }
    public function set callBack ( cb : Function ) : void {
      this._callBack = cb;
    }
    /**
     * 			The arguments to be passed to the call back function when executed.
     */
    public function get arguments (  ) : Array {
      return this._arguments;
    }
    public function set arguments ( args : Array ) : void {
      this._arguments = args;
    }
    /**
     * 			@exclude
     */
    override public function dispatchEvent ( event : Event ) : Boolean {
      if ( this.willTrigger(event.type) ) {
        return super.dispatchEvent(event);
        
      }
      
      return true;
    }
    /**
     * 			@exclude
     */
    override public function addEventListener ( type : String, listener : Function, useCapture : Boolean  = false, priority : int  = 0, useWeakReference : Boolean  = false ) : void {
      super.addEventListener(type, listener, useCapture, priority, useWeakReference);
      this._listenerManager.addEventListener(type, listener, useCapture, priority, useWeakReference);
    }
    /**
     * 			@exclude
     */
    override public function removeEventListener ( type : String, listener : Function, useCapture : Boolean  = false ) : void {
      super.removeEventListener(type, listener, useCapture);
      this._listenerManager.removeEventListener(type, listener, useCapture);
    }
    public function removeEventsForType ( type : String ) : void {
      this._listenerManager.removeEventsForType(type);
    }
    public function removeEventsForListener ( listener : Function ) : void {
      this._listenerManager.removeEventsForListener(listener);
    }
    public function removeEventListeners (  ) : void {
      this._listenerManager.removeEventListeners();
    }
    public function getTotalEventListeners ( type : String  = null ) : uint {
      return this._listenerManager.getTotalEventListeners(type);
    }
    public function get destroyed (  ) : Boolean {
      return this._isDestroyed;
    }
    public function destroy (  ) : void {
      this.reset();
      
      this._listenerManager.destroy();
      
      super.removeEventListener(TimerEvent.TIMER, this._timerHandler);
      
      this._isDestroyed = true;
    }
    protected function _timerHandler ( e : TimerEvent ) : void {
      this._callBack.apply(null, this._arguments);
    }
  }
}




