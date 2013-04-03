package org.casalib.events{
  
  import flash.events.IEventDispatcher;
  import flash.events.EventDispatcher;
  import org.casalib.core.IDestroyable;
  import flash.events.Event;
  
  /**
   * 		Extends <code>EventDispatcher</code> to allow for simple and quick removal of event listeners.
   * 		
   * 		@author Aaron Clinger
   * 		@version 02/11/10
   */
  public class RemovableEventDispatcher extends EventDispatcher implements IRemovableEventDispatcher, IDestroyable {
    protected var _listenerManager : ListenerManager ;
    protected var _isDestroyed : Boolean ;
    /**
     * 			Creates a new RemovableEventDispatcher.
     * 			
     * 			@param target: The target object for events dispatched to the EventDispatcher object.
     */
    public function RemovableEventDispatcher( target : IEventDispatcher  = null ){
      super(target);
      
      this._listenerManager = ListenerManager.getManager(this);
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
      this._listenerManager.destroy();
      
      this._isDestroyed = true;
    }
  }
}




