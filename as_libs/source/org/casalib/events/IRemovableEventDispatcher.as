package org.casalib.events{
  
  import flash.events.IEventDispatcher;
  
  /**
   * 		@author Aaron Clinger
   * 		@version 02/11/10
   */
  public interface IRemovableEventDispatcher extends IEventDispatcher {
    /**
     * 			Removes all events of a specific type.
     * 			
     * 			@param type: The type of event.
     */
    function removeEventsForType ( type : String ) : void ;
    /**
     * 			Removes all events that report to the specified listener.
     * 			
     * 			@param listener: The listener function that processes the event.
     */
    function removeEventsForListener ( listener : Function ) : void ;
    /**
     * 			Removes all event listeners.
     */
    function removeEventListeners (  ) : void ;
    /**
     * 			Reports the number of listeners for a specific event or the total listeners for all events.
     * 			
     * 			@param type: The name of event or <code>null</code> for all events.
     * 			@return The number of listeners.
     */
    function getTotalEventListeners ( type : String  = null ) : uint ;
  }
}




