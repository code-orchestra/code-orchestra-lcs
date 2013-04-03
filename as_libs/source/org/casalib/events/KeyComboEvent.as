package org.casalib.events{
  
  import flash.events.Event;
  import org.casalib.ui.KeyCombo;
  
  /**
   * 		An event dispatched in response to a user {@link KeyComboEvent#DOWN holding}, {@link KeyComboEvent#RELEASE releasing} or {@link KeyComboEvent#SEQUENCE typing} a combination of keys.
   * 		
   * 		@author Aaron Clinger
   * 		@version 01/19/10
   */
  public class KeyComboEvent extends Event {
    public static const DOWN : String  = 'down' ;
    public static const RELEASE : String  = 'release' ;
    public static const SEQUENCE : String  = 'sequence' ;
    protected var _keyCombo : KeyCombo ;
    /**
     * 			Creates a new KeyComboEvent.
     * 			
     * 			@param type: The type of event.
     * 			@param bubbles: Determines whether the Event object participates in the bubbling stage of the event flow.
     * 			@param cancelable: Determines whether the Event object can be canceled.
     */
    public function KeyComboEvent( type : String, bubbles : Boolean  = false, cancelable : Boolean  = false ){
      super(type, bubbles, cancelable);
    }
    /**
     * 			The {@link KeyCombo} that contains the key codes that triggered the event.
     */
    public function get keyCombo (  ) : KeyCombo {
      return this._keyCombo;
    }
    public function set keyCombo ( keyCombo : KeyCombo ) : void {
      this._keyCombo = keyCombo;
    }
    /**
     * 			@return A string containing all the properties of the event.
     */
    override public function toString (  ) : String {
      return formatToString('KeyComboEvent', 'type', 'bubbles', 'cancelable', 'keyCombo');
    }
    /**
     * 			@return Duplicates an instance of the event.
     */
    override public function clone (  ) : Event {
      var e : KeyComboEvent  = new KeyComboEvent(this.type, this.bubbles, this.cancelable);
      e.keyCombo = this.keyCombo;
      
      return e;
    }
  }
}




