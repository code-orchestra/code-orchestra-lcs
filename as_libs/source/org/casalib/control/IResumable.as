package org.casalib.control{
  
  
  /**
   * 		@author Aaron Clinger
   * 		@author Mike Creighton
   * 		@version 10/27/08
   */
  public interface IResumable extends IRunnable {
    /**
     * 			Resumes the process from <code>stop()</code>.
     */
    function resume (  ) : void ;
  }
}




