package org.casalib.util{
  
  import flash.net.URLRequest;
  import org.casalib.errors.ArguementTypeError;
  import flash.external.ExternalInterface;
  import flash.net.navigateToURL;
  
  /**
   * 		Simplifies <code>navigateToURL</code> and <code>window.open</code> requests.
   * 		
   * 		@author Aaron Clinger
   * 		@version 02/13/10
   */
  public class NavigateUtil extends Object {
    public static const WINDOW_SELF : String  = '_self' ;
    public static const WINDOW_BLANK : String  = '_blank' ;
    public static const WINDOW_PARENT : String  = '_parent' ;
    public static const WINDOW_TOP : String  = '_top' ;
    /**
     * 			Simlifies <code>navigateToURL</code> by allowing you to either use a <code>String</code> or an <code>URLRequest</code> reference to the URL. This method also helps prevent pop-up blocking by trying to use {@link #openWindow} before calling <code>navigateToURL</code>.
     * 			
     * 			@param request: A <code>String</code> or an <code>URLRequest</code> reference to the URL you wish to open/navigate to.
     * 			@param window: The browser window or HTML frame in which to display the URL indicated by the <code>request</code> parameter.
     * 			@throws ArguementTypeError if you pass a type other than a <code>String</code> or <code>URLRequest</code> to parameter <code>request</code>.
     */
    public static function openUrl ( request : *, window : String  = NavigateUtil.WINDOW_SELF ) : void {
      if ( request is String ) {
        request = new URLRequest(request);
      } else if ( !(request is URLRequest) ) {
        throw new ArguementTypeError('request');
        
      }
      
      if ( window == NavigateUtil.WINDOW_BLANK && ExternalInterface.available && !(LocationUtil.isIde()) && request.data == null ) {
        if ( NavigateUtil.openWindow(request.url, window) ) {
          return ;
          
        }
        
      }
      
      navigateToURL(request, window);
    }
    /**
     * 			A Flash wrapper for JavaScript’s <code>window.open</code>.
     * 			
     * 			@param url: Specifies the URL you wish to open/navigate to.
     * 			@param window: The browser window or HTML frame in which to display the URL indicated by the <code>url</code> parameter.
     * 			@param features: Defines the various window features to be included.
     * 			@return Returns <code>true</code> if the window was successfully created; otherwise <code>false</code>.
     * 			@see <a href="http://google.com/search?q=JavaScript+window.open+documentation">JavaScript documentation for window.open</a>.
     */
    public static function openWindow ( url : String, window : String  = NavigateUtil.WINDOW_BLANK, features : String  = "" ) : Boolean {
      if ( ExternalInterface.available ) {
        try {
          return ExternalInterface.call("function casaOpenWindow(url, windowOrName, features) { return window.open(url, windowOrName, features) != null; }", url, (window == NavigateUtil.WINDOW_BLANK) ? 'casaWindow' + int(1000 * Math.random()) : window, features);
        } catch ( e : Error ) {
          
        }
      }
      
      return false;
    }
  }
}




