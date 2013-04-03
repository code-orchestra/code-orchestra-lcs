package org.casalib.util{
  
  import org.casalib.load.LoadItem;
  import flash.net.URLRequest;
  import org.casalib.errors.ArguementTypeError;
  import org.casalib.load.ImageLoad;
  import org.casalib.load.DataLoad;
  import org.casalib.load.SwfLoad;
  import org.casalib.load.VideoLoad;
  import org.casalib.load.AudioLoad;
  import flash.net.URLLoaderDataFormat;
  
  /**
   * 		Utilities for working with LoadItems.
   * 		
   * 		@author Aaron Clinger
   * 		@version 02/12/10
   */
  public class LoadItemUtil extends Object {
    public static var AUDIO_EXTENSIONS : Array  = new Array('f4a', 'f4b', 'mp3') ; //< The default list of audio file extensions.
    public static var BINARY_EXTENSIONS : Array  = new Array('3ds', 'md2', 'zip') ; //< The default list of binary file extensions.
    public static var IMAGE_EXTENSIONS : Array  = new Array('gif', 'jpg', 'jpeg', 'png') ; //< The default list of image file extensions.
    public static var SWF_EXTENSIONS : Array  = new Array('swf') ; //< The default list of SWF file extensions.
    public static var TEXT_EXTENSIONS : Array  = new Array('asp', 'aspx', 'css', 'dae', 'html', 'js', 'jsp', 'php', 'py', 'txt', 'xhtml', 'xml') ; //< The default list of text file extensions.
    public static var VIDEO_EXTENSIONS : Array  = new Array('f4p', 'f4v', 'flv', 'mov', 'mp4') ; //< The default list of video file extensions.
    /**
     * 			Creates load class ({@link AudioLoad}, {@link DataLoad}, {@link ImageLoad}, {@link SwfLoad} or {@link VideoLoad}) based on the provided file's extension.
     * 			
     * 			@param request: A <code>String</code> or an <code>URLRequest</code> reference to the file you wish to load.
     * 			@return The base <code>LoadItem</code> class being used to load the requested file. Can be cast as a specific load class.
     * 			@throws ArguementTypeError if you pass a type other than a <code>String</code> or an <code>URLRequest</code> to parameter <code>request</code>.
     * 			@throws <code>Error</code> if unable to determine the file type by its extention.
     * 			@example
     * 				<code>
     * 					this._groupLoad = new GroupLoad();
     * 					this._groupLoad.addLoad(LoadItemUtil.createLoadItem("test.flv"));
     * 					this._groupLoad.addLoad(LoadItemUtil.createLoadItem("test.xml"));
     * 					this._groupLoad.addLoad(LoadItemUtil.createLoadItem("test.mp3"));
     * 					this._groupLoad.addLoad(LoadItemUtil.createLoadItem("test.jpg"));
     * 					this._groupLoad.addEventListener(LoadEvent.COMPLETE, this._onComplete);
     * 					this._groupLoad.start();
     * 				</code>
     */
    public static function createLoadItem ( request : * ) : LoadItem {
      var file : String;
      
      if ( request is URLRequest ) {
        file = request.url;
      } else if ( request is String ) {
        file = request;
      }else{
        throw new ArguementTypeError('request');
        
      }
      
      file = file.indexOf('?') == -1 ? file : file.substring(0, file.indexOf('?'));
      
      const ext : String  = file.substr(file.lastIndexOf('.') + 1).toLowerCase();
      
      if ( LoadItemUtil.IMAGE_EXTENSIONS.indexOf(ext) > -1 ) {
        return new ImageLoad(request);
      } else if ( LoadItemUtil.TEXT_EXTENSIONS.indexOf(ext) > -1 ) {
        return new DataLoad(request);
      } else if ( LoadItemUtil.SWF_EXTENSIONS.indexOf(ext) > -1 ) {
        return new SwfLoad(request);
      } else if ( LoadItemUtil.VIDEO_EXTENSIONS.indexOf(ext) > -1 ) {
        return new VideoLoad(request);
      } else if ( LoadItemUtil.AUDIO_EXTENSIONS.indexOf(ext) > -1 ) {
        return new AudioLoad(request);
      } else if ( LoadItemUtil.BINARY_EXTENSIONS.indexOf(ext) > -1 ) {
        return new DataLoad(request, URLLoaderDataFormat.BINARY);
        
      }
      
      throw new Error('Could not determine file type of: ' + file);
    }
  }
}




