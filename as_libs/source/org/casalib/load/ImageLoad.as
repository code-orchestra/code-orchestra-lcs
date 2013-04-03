package org.casalib.load{
  
  import flash.system.LoaderContext;
  import flash.display.Bitmap;
  import flash.display.BitmapData;
  
  /**
   * 		Provides an easy and standardized way to load images.
   * 		
   * 		@author Aaron Clinger
   * 		@version 02/13/10
   * 		@example
   * 			<code>
   * 				package {
   * 					import org.casalib.display.CasaMovieClip;
   * 					import org.casalib.events.LoadEvent;
   * 					import org.casalib.load.ImageLoad;
   * 					
   * 					
   * 					public class MyExample extends CasaMovieClip {
   * 						protected var _imageLoad:ImageLoad;
   * 						
   * 						
   * 						public function MyExample() {
   * 							super();
   * 							
   * 							this._imageLoad = new ImageLoad("test.jpg");
   * 							this._imageLoad.addEventListener(LoadEvent.COMPLETE, this._onComplete);
   * 							this._imageLoad.start();
   * 						}
   * 						
   * 						protected function _onComplete(e:LoadEvent):void {
   * 							this.addChild(this._imageLoad.contentAsBitmap);
   * 						}
   * 					}
   * 				}
   * 			</code>
   */
  public class ImageLoad extends CasaLoader {
    /**
     * 			Creates and defines an ImageLoad.
     * 			
     * 			@param request: A <code>String</code> or an <code>URLRequest</code> reference to the file you wish to load.
     * 			@param context: An optional LoaderContext object.
     * 			@throws ArguementTypeError if you pass a type other than a <code>String</code> or an <code>URLRequest</code> to parameter <code>request</code>.
     * 			@throws <code>Error</code> if you try to load an empty <code>String</code> or <code>URLRequest</code>.
     */
    public function ImageLoad( request : *, context : LoaderContext  = null ){
      super(request, context);
    }
    /**
     * 			The data received from the DataLoad data typed as Bitmap. Available after load is complete.
     * 			
     * 			@throws <code>Error</code> if method is called before the SWF has loaded.
     * 			@throws <code>Error</code> if method cannot convert content to a Bitmap.
     */
    public function get contentAsBitmap (  ) : Bitmap {
      if ( !(this.loaded) || this.loaderInfo.contentType == CasaLoader.FLASH_CONTENT_TYPE ) {
        throw new Error('Cannot convert content to a Bitmap.');
        
      }
      
      return this.content as Bitmap;
    }
    /**
     * 			The data received from the DataLoad data typed as BitmapData. Available after load is complete.
     * 			
     * 			@throws <code>Error</code> if method is called before the SWF has loaded.
     * 			@throws <code>Error</code> if method cannot convert content to BitmapData.
     */
    public function get contentAsBitmapData (  ) : BitmapData {
      if ( !(this.loaded) || this.loaderInfo.contentType == CasaLoader.FLASH_CONTENT_TYPE ) {
        throw new Error('Cannot convert content to BitmapData.');
        
      }
      
      return this.contentAsBitmap.bitmapData;
    }
  }
}




