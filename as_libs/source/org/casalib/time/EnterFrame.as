package org.casalib.time{
  
  import org.casalib.events.RemovableEventDispatcher;
  import flash.display.Shape;
  import flash.events.Event;
  
  [Event(name="enterFrame", type="flash.events.Event")]
  /**
   * 		Creates a centralized <code>enterFrame</code> event. Also enables classes that do not extend a display object to react to an <code>enterFrame</code> event.
   * 		
   * 		@author Aaron Clinger
   * 		@author Mike Creighton
   * 		@version 09/06/09
   * 		@example
   * 			<code>
   * 				package {
   * 					import flash.events.Event;
   * 					import org.casalib.display.CasaMovieClip;
   * 					import org.casalib.time.EnterFrame;
   * 					
   * 					
   * 					public class MyExample extends CasaMovieClip {
   * 						protected var _pulseInstance:EnterFrame;
   * 						
   * 						
   * 						public function MyExample() {
   * 							super();
   * 							
   * 							this._pulseInstance = EnterFrame.getInstance();
   * 							this._pulseInstance.addEventListener(Event.ENTER_FRAME, this._onFrameFire);
   * 						}
   * 						
   * 						protected function _onFrameFire(e:Event):void {
   * 							trace("I will be called every frame.");
   * 						}
   * 					}
   * 				}
   * 			</code>
   */
  public class EnterFrame extends RemovableEventDispatcher {
    protected static var _instance : EnterFrame ;
    protected static var _pulseShape : Shape ;
    /**
     * 			@exclude
     */
    public function EnterFrame( singletonEnforcer : SingletonEnforcer ){
      super();
      this._createBeacon();
    }
    /**
     * 			@return {@link EnterFrame} instance.
     */
    public static function getInstance (  ) : EnterFrame {
      if ( EnterFrame._instance == null ) {
        EnterFrame._instance = new EnterFrame(new SingletonEnforcer());
        
      }
      
      return EnterFrame._instance;
    }
    /**
     * 			@throws <code>Error</code> if called. Cannot destroy a singleton.
     */
    override public function destroy (  ) : void {
      throw new Error('Cannot destroy a singleton.');
    }
    protected function _createBeacon (  ) : void {
      EnterFrame._pulseShape = new Shape();
      EnterFrame._pulseShape.addEventListener(Event.ENTER_FRAME, this._handlePulseEnterFrame);
    }
    /**
     * 			@sends Event#ENTER_FRAME - Dispatched when a new frame is entered.
     */
    protected function _handlePulseEnterFrame ( e : Event ) : void {
      this.dispatchEvent(new Event(Event.ENTER_FRAME));
    }
  }
}




class SingletonEnforcer extends Object {
  public function SingletonEnforcer(  ){
    
  }
}
