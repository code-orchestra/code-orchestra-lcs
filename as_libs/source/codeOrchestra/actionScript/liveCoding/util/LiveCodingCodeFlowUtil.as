package codeOrchestra.actionScript.liveCoding.util{
  
  import flash.display.Sprite;
  import flash.events.Event;
  
  [AlwaysUsed]
  public class LiveCodingCodeFlowUtil {
    private static var maxRecursionCount : int  = 10000 ;
    private static var maxLoopCount : int  = 10000 ;
    
    private static var lastMethod : String ;
    private static var lastLoop : String ;
    
    private static var recursionCount : int ;
    private static var loopCount : int ;
    
    private static var enterFrameCounter : Sprite ;
    public function LiveCodingCodeFlowUtil(  ){
      
    }
    public static function setMaxLoopCount ( value : int ) : void {
      maxLoopCount = value;
    }
    public static function setMaxRecursionCount ( value : int ) : void {
      maxRecursionCount = value;
    }
    public static function checkRecursion ( method : String ) : void {
      initCounter();
      if ( method != lastMethod ) {
        lastMethod = method;
        recursionCount = 0;
      }
      recursionCount++;
      if ( recursionCount > maxRecursionCount ) {
        throw new Error("Infinite recursion: " + method + ".");
      }
    }
    public static function checkLoop ( loop : String ) : void {
      initCounter();
      if ( loop != lastLoop ) {
        lastLoop = loop;
        loopCount = 0;
      }
      loopCount++;
      if ( loopCount > maxLoopCount ) {
        throw new Error("Infinite loop.");
      }
    }
    public static function initCounter (  ) : void {
      if ( !(enterFrameCounter) ) {
        enterFrameCounter = new Sprite();
        enterFrameCounter.addEventListener(Event.ENTER_FRAME,         function ( e : Event ) : void {
          loopCount = 0;
          lastLoop = "";
          recursionCount = 0;
          lastLoop = "";
        });
      }
    }
  }
}



