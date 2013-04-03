package org.casalib.time{
  
  import org.casalib.control.IResumable;
  import flash.utils.getTimer;
  
  /**
   * 		Simple stopwatch class that records elapsed time in milliseconds.
   * 		
   * 		@author Aaron Clinger
   * 		@author Mike Creighton
   * 		@version 02/11/11
   * 		@example
   * 			<code>
   * 				package {
   * 					import org.casalib.display.CasaMovieClip;
   * 					import org.casalib.time.Stopwatch;
   * 					
   * 					
   * 					public class MyExample extends CasaMovieClip {
   * 						
   * 						public function MyExample() {
   * 							super();
   * 							
   * 							var stopwatch:Stopwatch = new Stopwatch();
   * 							stopwatch.start();
   * 							
   * 							var l:uint = 1000000;
   * 							while (l--) {
   * 								doSomething();
   * 							}
   * 							
   * 							trace(stopwatch.time);
   * 						}
   * 						
   * 						public function doSomething():void {
   * 							
   * 						}
   * 					}
   * 				}
   * 			</code>
   */
  public class Stopwatch extends Object implements IResumable {
    protected var _elapsedTime : int ;
    protected var _startTime : int ;
    protected var _isRunning : Boolean ;
    /**
     * 			Creates a new Stopwatch.
     */
    public function Stopwatch(  ){
      super();
      
      this._startTime = 0;
      this._elapsedTime = 0;
    }
    /**
     * 			Starts stopwatch and resets previous elapsed time.
     */
    public function start (  ) : void {
      this._elapsedTime = 0;
      this._startTime = this._timer;
      this._isRunning = true;
    }
    /**
     * 			Stops stopwatch.
     */
    public function stop (  ) : void {
      this._elapsedTime = this.time;
      this._startTime = 0;
      this._isRunning = false;
    }
    /**
     * 			Resumes stopwatch from {@link Stopwatch#stop}.
     */
    public function resume (  ) : void {
      if ( !(this.running) ) {
        this._startTime = this._timer;
        this._isRunning = true;
      }
    }
    /**
     * 			Determines if the stopwatch is currently running <code>true</code>, or if it isn't <code>false</code>.
     */
    public function get running (  ) : Boolean {
      return this._isRunning;
    }
    /**
     * 			Gets the time elapsed since {@link Stopwatch#start} or until {@link Stopwatch#stop} was called.
     * 			
     * 			@return Returns the elapsed time in milliseconds.
     * 			@usageNote Can be called before or after calling {@link Stopwatch#stop}.
     */
    public function get time (  ) : int {
      return (this._startTime != 0) ? this._timer - this._startTime + this._elapsedTime : this._elapsedTime;
    }
    protected function get _timer (  ) : int {
      return getTimer();
    }
  }
}




