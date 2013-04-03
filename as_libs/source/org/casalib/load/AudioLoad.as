package org.casalib.load{
  
  import flash.media.SoundLoaderContext;
  import flash.media.Sound;
  import flash.events.Event;
  import flash.events.IOErrorEvent;
  import flash.events.ProgressEvent;
  import flash.events.IEventDispatcher;
  
  [Event(name="id3", type="flash.events.Event")]
  /**
   * 		Provides an easy and standardized way to load audio files.
   * 		
   * 		@author Aaron Clinger
   * 		@version 02/13/10
   * 		@example
   * 			<code>
   * 				package {
   * 					import org.casalib.display.CasaMovieClip;
   * 					import org.casalib.events.LoadEvent;
   * 					import org.casalib.load.AudioLoad;
   * 					
   * 					
   * 					public class MyExample extends CasaMovieClip {
   * 						protected var _audioLoad:AudioLoad;
   * 						
   * 						
   * 						public function MyExample() {
   * 							super();
   * 							
   * 							this._audioLoad = new AudioLoad("test.mp3");
   * 							this._audioLoad.addEventListener(LoadEvent.COMPLETE, this._onComplete);
   * 							this._audioLoad.start();
   * 						}
   * 						
   * 						protected function _onComplete(e:LoadEvent):void {
   * 							this._audioLoad.sound.play();
   * 						}
   * 					}
   * 				}
   * 			</code>
   */
  public class AudioLoad extends LoadItem {
    protected var _context : SoundLoaderContext ;
    protected var _isFirstLoad : Boolean ;
    /**
     * 			Creates and defines an AudioLoad.
     * 			
     * 			@param request: A <code>String</code> or an <code>URLRequest</code> reference to the file you wish to load.
     * 			@param context: An optional SoundLoaderContext object.
     * 			@throws ArguementTypeError if you pass a type other than a <code>String</code> or an <code>URLRequest</code> to parameter <code>request</code>.
     * 			@throws <code>Error</code> if you try to load an empty <code>String</code> or <code>URLRequest</code>.
     */
    public function AudioLoad( request : *, context : SoundLoaderContext  = null ){
      super(new Sound(), request);
      
      this._context = context;
      this._isFirstLoad = true;
      
      this._initListeners(this._loadItem);
    }
    /**
     * 			The Sound object.
     */
    public function get sound (  ) : Sound {
      return this._loadItem as Sound;
    }
    override public function destroy (  ) : void {
      this._dispatcher.removeEventListener(Event.ID3, this.dispatchEvent);
      
      super.destroy();
    }
    override protected function _load (  ) : void {
      if ( !(this._isFirstLoad) ) {
        this._dispatcher.removeEventListener(Event.COMPLETE, this._onComplete);
        this._dispatcher.removeEventListener(Event.OPEN, this._onOpen);
        this._dispatcher.removeEventListener(IOErrorEvent.IO_ERROR, this._onLoadError);
        this._dispatcher.removeEventListener(ProgressEvent.PROGRESS, this._onProgress);
        this._dispatcher.removeEventListener(Event.ID3, this.dispatchEvent);
        
        this._loadItem = new Sound();
        this._initListeners(this._loadItem);
      }
      
      this._isFirstLoad = false;
      
      this._loadItem.load(this._request, this._context);
    }
    /**
     * 			@sends Event#ID3 - Dispatched when ID3 data is available.
     */
    override protected function _initListeners ( dispatcher : IEventDispatcher ) : void {
      super._initListeners(dispatcher);
      
      this._dispatcher.addEventListener(Event.ID3, this.dispatchEvent, false, 0, true);
    }
  }
}




