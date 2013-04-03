package org.casalib.process{
  
  import org.casalib.events.RemovableEventDispatcher;
  import org.casalib.control.IRunnable;
  import org.casalib.events.ProcessEvent;
  
  [Event(name="processStart", type="org.casalib.events.ProcessEvent")]
  [Event(name="processStop", type="org.casalib.events.ProcessEvent")]
  [Event(name="processComplete", type="org.casalib.events.ProcessEvent")]
  /**
   * 		Base process class. Process is not designed to be used on its own and needs to be extended to function.
   * 		
   * 		@author Aaron Clinger
   * 		@version 02/11/10
   */
  public class Process extends RemovableEventDispatcher implements IRunnable {
    public static var NORM_PRIORITY : int  = 0 ; //< The default priority for all Process instances.
    protected var _priority : uint ;
    protected var _isRunning : Boolean ;
    protected var _hasCompleted : Boolean ;
    /**
     * 			Creates a new Process.
     */
    public function Process(  ){
      super();
      
      this.priority = Process.NORM_PRIORITY;
    }
    /**
     * 			@sends ProcessEvent#START - Dispatched when process starts.
     */
    public function start (  ) : void {
      this._isRunning = true;
      this._hasCompleted = false;
      
      this.dispatchEvent(new ProcessEvent(ProcessEvent.START));
    }
    /**
     * 			@sends ProcessEvent#STOP - Dispatched when process is stopped.
     */
    public function stop (  ) : void {
      this._isRunning = false;
      
      this.dispatchEvent(new ProcessEvent(ProcessEvent.STOP));
    }
    /**
     * 			Determines if the process is currently running <code>true</code>, or if it isn't <code>false</code>.
     */
    public function get running (  ) : Boolean {
      return this._isRunning;
    }
    /**
     * 			Determines if the process has completed <code>true</code>, or if it hasn't <code>false</code>.
     */
    public function get completed (  ) : Boolean {
      return this._hasCompleted;
    }
    /**
     * 			The priority relative to other processes. The higher priority processes will take precedence over lower priority processes in a {@link ProcessGroup}.
     */
    public function get priority (  ) : int {
      return this._priority;
    }
    public function set priority ( priority : int ) : void {
      this._priority = priority;
    }
    override public function destroy (  ) : void {
      if ( this.running ) {
        this.stop();
        
      }
      
      super.destroy();
    }
    /**
     * 			@sends ProcessEvent#COMPLETE - Dispatched when process completes.
     */
    protected function _complete (  ) : void {
      this._isRunning = false;
      this._hasCompleted = true;
      
      this.dispatchEvent(new ProcessEvent(ProcessEvent.COMPLETE));
    }
  }
}




