package org.casalib.time{
  
  
  /**
   * 		Functions exactly like {@link Stopwatch} but uses {@link FrameTime} as the timing mechanism.
   * 		
   * 		@author Aaron Clinger
   * 		@author Mike Creighton
   * 		@version 10/11/08
   */
  public class FrameTimeStopwatch extends Stopwatch {
    /**
     * 			Creates a new FrameTimeStopwatch.
     */
    public function FrameTimeStopwatch(  ){
      super();
    }
    override protected function get _timer (  ) : int {
      return FrameTime.getInstance().time;
    }
  }
}




