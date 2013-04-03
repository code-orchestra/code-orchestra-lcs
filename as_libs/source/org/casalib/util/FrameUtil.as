package org.casalib.util{
  
  import flash.display.MovieClip;
  import org.casalib.errors.ArguementTypeError;
  
  /**
   * 		Utilities for determining label positions and adding and removing frame scripts.
   * 		
   * 		@author Mike Creighton
   * 		@author Aaron Clinger
   * 		@version 02/12/10
   */
  public class FrameUtil extends Object {
    /**
     * 			Determines the frame number for the specified label.
     * 			
     * 			@param target: The MovieClip to search for the frame label in.
     * 			@param label: The name of the frame label.
     * 			@return The frame number of the label or <code>-1</code> if the frame label was not found.
     */
    public static function getFrameNumberForLabel ( target : MovieClip, label : String ) : int {
      var labels : Array  = target.currentLabels;
      var l : int  = labels.length;
      
      while ( l-- ) {
        if ( labels[l].name == label ) {
          return labels[l].frame;
          
        }
        
      }
      
      return -1;
    }
    /**
     * 			Calls a specified method when a specific frame is reached in a MovieClip timeline.
     * 			
     * 			@param target: The MovieClip that contains the <code>frame</code>.
     * 			@param frame: The frame to be notified when reached. Can either be a frame number (<code>uint</code>), or the frame label (<code>String</code>).
     * 			@param notify: The function that will be called when the frame is reached.
     * 			@return Returns <code>true</code> if the frame was found; otherwise <code>false</code>.
     * 			@throws ArguementTypeError if you pass a type other than a <code>String</code> or <code>uint</code> to parameter <code>frame</code>.
     */
    public static function addFrameScript ( target : MovieClip, frame : *, notify : Function ) : Boolean {
      if ( frame is String ) {
        frame = FrameUtil.getFrameNumberForLabel(target, frame);
      } else if ( !(frame is uint) ) {
        throw new ArguementTypeError('frame');
        
      }
      
      if ( frame == -1 || frame == 0 || frame > target.totalFrames ) {
        return false;
        
      }
      
      target.addFrameScript(frame - 1, notify);
      
      return true;
    }
    /**
     * 			Removes a frame from triggering/calling a function when reached.
     * 			
     * 			@param target: The MovieClip that contains the <code>frame</code>.
     * 			@param frame: The frame to remove notification from. Can either be a frame number (<code>uint</code>), or the frame label (<code>String</code>).
     * 			@throws ArguementTypeError if you pass a type other than a <code>String</code> or <code>uint</code> to parameter <code>frame</code>.
     */
    public static function removeFrameScript ( target : MovieClip, frame : * ) : void {
      if ( frame is String ) {
        frame = FrameUtil.getFrameNumberForLabel(target, frame);
      } else if ( !(frame is uint) ) {
        throw new ArguementTypeError('frame');
        
      }
      
      if ( frame == -1 || frame == 0 || frame > target.totalFrames ) {
        return ;
        
      }
      
      target.addFrameScript(frame - 1, null);
    }
  }
}




