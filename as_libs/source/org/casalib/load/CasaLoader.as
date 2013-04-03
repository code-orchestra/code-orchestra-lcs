package org.casalib.load{
  
  import flash.system.LoaderContext;
  import flash.display.Loader;
  import flash.display.DisplayObject;
  import flash.display.LoaderInfo;
  import flash.events.Event;
  import flash.events.HTTPStatusEvent;
  import flash.events.SecurityErrorEvent;
  import flash.events.IEventDispatcher;
  
  [Event(name="init", type="flash.events.Event")]
  [Event(name="unload", type="flash.events.Event")]
  [Event(name="httpStatus", type="flash.events.HTTPStatusEvent")]
  [Event(name="securityError", type="flash.events.SecurityErrorEvent")]
  /**
   * 		Wraps <code>Loader</code> and extends from {@link LoadItem} and {@link Process}.
   * 		
   * 		In almost all cases you will want to use {@link ImageLoad} or {@link SwfLoad} instead of this class.
   * 		
   * 		@author Aaron Clinger
   * 		@version 02/13/10
   * 		@example
   * 			<code>
   * 				package {
   * 					import org.casalib.display.CasaMovieClip;
   * 					import org.casalib.events.LoadEvent;
   * 					import org.casalib.load.CasaLoader;
   * 					
   * 					
   * 					public class MyExample extends CasaMovieClip {
   * 						protected var _casaLoader:CasaLoader;
   * 						
   * 						
   * 						public function MyExample() {
   * 							super();
   * 							
   * 							this._casaLoader = new CasaLoader("test.jpg");
   * 							this._casaLoader.addEventListener(LoadEvent.COMPLETE, this._onComplete);
   * 							this._casaLoader.start();
   * 						}
   * 						
   * 						protected function _onComplete(e:LoadEvent):void {
   * 							this.addChild(this._casaLoader.loader);
   * 						}
   * 					}
   * 				}
   * 			</code>
   */
  public class CasaLoader extends LoadItem {
    public static const FLASH_CONTENT_TYPE : String  = 'application/x-shockwave-flash' ;
    public static const JPEG_CONTENT_TYPE : String  = 'image/jpeg' ;
    public static const GIF_CONTENT_TYPE : String  = 'image/gif' ;
    public static const PNG_CONTENT_TYPE : String  = 'image/png' ;
    protected var _context : LoaderContext ;
    /**
     * 			Creates and defines a CasaLoader.
     * 			
     * 			@param request: A <code>String</code> or an <code>URLRequest</code> reference to the file you wish to load.
     * 			@param context: An optional LoaderContext object.
     * 			@throws ArguementTypeError if you pass a type other than a <code>String</code> or an <code>URLRequest</code> to parameter <code>request</code>.
     * 			@throws <code>Error</code> if you try to load an empty <code>String</code> or <code>URLRequest</code>.
     */
    public function CasaLoader( request : *, context : LoaderContext  = null ){
      super(new Loader(), request);
      
      this._context = context;
      
      this._initListeners(this.loaderInfo);
    }
    /**
     * 			The Loader being used to load the image or SWF.
     */
    public function get loader (  ) : Loader {
      return this._loadItem as Loader;
    }
    /**
     * 			The content received from the CasaLoader. Available after load is complete.
     * 			
     * 			@throws <code>Error</code> if method is called before the SWF has loaded.
     */
    public function get content (  ) : DisplayObject {
      if ( !(this.loaded) ) {
        throw new Error('Cannot access an external asset until the SWF has loaded.');
        
      }
      
      return this.loader.content;
    }
    /**
     * 			The LoaderInfo corresponding to the object being loaded.
     */
    public function get loaderInfo (  ) : LoaderInfo {
      return this._loadItem.contentLoaderInfo;
    }
    /**
     * 			@exclude
     */
    override public function get bytesTotal (  ) : Number {
      return (this._loadItem.contentLoaderInfo.bytesTotal == 0 && this.bytesLoaded != 0) ? Number.POSITIVE_INFINITY : this._loadItem.contentLoaderInfo.bytesTotal;
    }
    /**
     * 			@exclude
     */
    override public function get bytesLoaded (  ) : uint {
      return this._loadItem.contentLoaderInfo.bytesLoaded;
    }
    override public function destroy (  ) : void {
      this._dispatcher.removeEventListener(Event.INIT, this.dispatchEvent);
      this._dispatcher.removeEventListener(Event.UNLOAD, this.dispatchEvent);
      this._dispatcher.removeEventListener(HTTPStatusEvent.HTTP_STATUS, this._onHttpStatus);
      this._dispatcher.removeEventListener(SecurityErrorEvent.SECURITY_ERROR, this.dispatchEvent);
      
      super.destroy();
    }
    override protected function _load (  ) : void {
      this._loadItem.load(this._request, this._context);
    }
    /**
     * 			@sends Event#INIT - Dispatched when the properties and methods of a loaded SWF file are accessible.
     * 			@sends Event#UNLOAD - Dispatched when <code>unload</code> is called.
     * 			@sends HTTPStatusEvent#HTTP_STATUS - Dispatched if class is able to detect and return the status code for the request.
     * 			@sends SecurityErrorEvent#SECURITY_ERROR - Dispatched if load is outside the security sandbox.
     */
    override protected function _initListeners ( dispatcher : IEventDispatcher ) : void {
      super._initListeners(dispatcher);
      
      this._dispatcher.addEventListener(Event.INIT, this.dispatchEvent, false, 0, true);
      this._dispatcher.addEventListener(Event.UNLOAD, this.dispatchEvent, false, 0, true);
      this._dispatcher.addEventListener(HTTPStatusEvent.HTTP_STATUS, this._onHttpStatus, false, 0, true);
      this._dispatcher.addEventListener(SecurityErrorEvent.SECURITY_ERROR, this.dispatchEvent, false, 0, true);
    }
  }
}




