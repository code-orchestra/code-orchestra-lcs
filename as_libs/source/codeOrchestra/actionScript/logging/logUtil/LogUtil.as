package codeOrchestra.actionScript.logging.logUtil{
  
  import flash.net.XMLSocket;
  import flash.events.Event;
  import flash.events.IOErrorEvent;
  import flash.events.SecurityErrorEvent;
  import flash.events.DataEvent;
  import flash.utils.setTimeout;
  import flash.utils.getTimer;
  import flash.utils.Dictionary;
  
  public class LogUtil {
    private static var socket : XMLSocket ;
    private static var messages : Array  = new Array() ;
    private static var scopes : Array  = new Array(["Main", 0]) ;
    private static const PORT : Number  = 6126 ;
    private static var hasError : Boolean  = false ;
    private static var tabsCount : int  = 0 ;
    private static var tabsString : String  = "" ;
    private static var listenersMap : Dictionary  = new Dictionary() ;
    private static var pondDisable : Boolean ;
    public function LogUtil(  ){
      
    }
    private static function initSocket (  ) : void {
      if ( hasError ) {
        return ;
      }
      try {
        socket = new XMLSocket();
        socket.addEventListener(Event.CONNECT, onConnect, false, 0, true);
        socket.addEventListener(IOErrorEvent.IO_ERROR,         function ( e : Event ) : void {
          closeSocketAfterError();
        }, false, 0, true);
        socket.addEventListener(IOErrorEvent.NETWORK_ERROR,         function ( e : Event ) : void {
          closeSocketAfterError();
        }, false, 0, true);
        socket.addEventListener(SecurityErrorEvent.SECURITY_ERROR,         function ( e : Event ) : void {
          closeSocketAfterError();
          e.stopImmediatePropagation();
        }, false, 0, true);
        socket.addEventListener(DataEvent.DATA,         function ( e : DataEvent ) : void {
          e.data = e.data.replace(/^\W+/, "");
          if ( /ping/.test(e.data) ) {
            if ( !(pondDisable) ) {
              pong();
            }
          }else{
            dispatchEvent(e.clone());
          }
        });
        socket.connect("localhost", PORT);
      } catch ( e : Error ) {
        closeSocketAfterError();
      }
    }
    public static function disablePong (  ) : void {
      pondDisable = true;
    }
    private static function internalTrace ( message : String ) : void {
      log("trace", "", "", "", message);
    }
    private static function onConnect ( event : Event ) : void {
      flush();
    }
    private static function onClose ( event : Event ) : void {
      var delay : Number  = Math.random() * 1000 + 2000;
      setTimeout(      function (  ) : void {
        initSocket();
      }, delay);
    }
    private static function flush (  ) : void {
      if ( socket == null ) {
        initSocket();
        return ;
      }
      if ( socket.connected ) {
        for each ( var e : XML in messages ) {
          socket.send(e);
        }
        messages = new Array();
      }
    }
    public static function enterLogScope ( scope : String, scopeId : String ) : void {
      if ( hasError ) {
        return ;
      }
      scopes.push([scope, scopeId]);
    }
    public static function exitLogScope ( scope : String, scopeId : String ) : void {
      if ( hasError ) {
        return ;
      }
      var oldScopes : Array  = scopes.concat([]);
      scopes = new Array();
      for each ( var e : Array in oldScopes ) {
        if ( !(e[0] == scope && e[1] == scopeId) ) {
          scopes.push(e);
        }
      }
    }
    [MixinReplacement]
    public static function log ( severity : String, nodeId : String, modelId : String, rootFQN : String, messageString : String, exception : Error  = null ) : String {
      if ( hasError ) {
        return messageString;
      }
      
      messageString = messageString.replace(/(\n|\r)+/, "\r");
      
      if ( /\r/.test(messageString) ) {
        for each ( var oneLine : * in messageString.split(/\r/) ) {
          if ( oneLine != undefined ) {
            log(severity, nodeId, modelId, rootFQN, oneLine, exception);
          }
        }
        return messageString;
      }
      
      XML.ignoreWhitespace = false;
      
      var xmlMessage : XML  =       
      <logMessage>
        <source nodeId={nodeId} modelReference={modelId}  />
        <message severity={severity}  >{"|" + tabsString + messageString}</message>
        <root>{rootFQN}</root>
        <timestamp>{getTimer()}</timestamp>
        <stackTrace>{exception ? exception.getStackTrace() : ""}</stackTrace>
        <scopes/>
      </logMessage>
;
      
      for each ( var scope : Array in scopes ) {
        xmlMessage.scopes[0].appendChild(<scope id={scope[0]}  >{scope[0]}</scope>)
      }
      
      messages.push(xmlMessage);
      
      flush();
      
      return messageString;
    }
    public static function append (  ) : void {
      tabsCount++;
      LogUtil.updateTabsString();
    }
    public static function disappend (  ) : void {
      tabsCount--;
      LogUtil.updateTabsString();
    }
    private static function updateTabsString (  ) : void {
      if ( tabsCount == 0 ) {
        tabsString = "";
        return ;
      }
      var result : String  = "";
      for ( var i : int  = 0 ; i < tabsCount ; i++ ) {
        result += "  ";
      }
      tabsString = result;
    }
    public static function selectNode ( modelId : String, nodeId : String ) : void {
      log("select-node", nodeId, modelId, "", "");
    }
    public static function startLiveCodingSession ( sessionId : String ) : void {
      log("start-live-coding-session", "", "", "", sessionId);
    }
    public static function pong (  ) : void {
      log("pong", "", "", "", "");
    }
    public static function closeSocketAfterError (  ) : void {
      hasError = true;
      if ( socket && socket.connected ) {
        socket.close();
      }
      socket = null;
    }
    private static function dispatchEvent ( event : Event ) : void {
      if ( listenersMap[event.type] ) {
        for each ( var l : * in listenersMap[event.type] ) {
          l(event);
        }
      }
    }
    public static function addEventListener ( type : String, listener : Function ) : void {
      if ( !(listenersMap[type]) ) {
        listenersMap[type] = new Array();
      }
      (listenersMap[type] as Array).push(listener);
    }
    public static function removeEventListener ( type : String, listener : Function ) : void {
      if ( listenersMap[type] ) {
        var newListeners : Array  = new Array();
        for each ( var e : * in listenersMap[type] ) {
          if ( e != listener ) {
            newListeners.push(e);
          }
        }
        listenersMap[type] = newListeners;
      }
    }
  }
}




