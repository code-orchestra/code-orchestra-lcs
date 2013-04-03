package org.casalib.util{
  
  import flash.display.DisplayObject;
  import flash.system.Capabilities;
  
  /**
   * 		Utilities for determining the location of the SWF and the type of runtime environment.
   * 		
   * 		@author Aaron Clinger
   * 		@version 02/29/10
   */
  public class LocationUtil extends Object {
    /**
     * 			Determines if the SWF is being served on the internet.
     * 			
     * 			@param location: DisplayObject to get location of.
     * 			@return Returns <code>true</code> if SWF is being served on the internet; otherwise <code>false</code>.
     * 			@example
     * 				<code>
     * 					trace(LocationUtil.isWeb(this.stage));
     * 				</code>
     */
    public static function isWeb ( location : DisplayObject ) : Boolean {
      return location.loaderInfo.url.substr(0, 4) == 'http';
    }
    /**
     * 			Detects if MovieClip's embed location matches passed domain.
     * 			
     * 			@param location: MovieClip to compare location of.
     * 			@param domain: Web domain.
     * 			@return Returns <code>true</code> if file's embed location matched passed domain; otherwise <code>false</code>.
     * 			@example
     * 				To check for domain:
     * 				<code>
     * 					trace(LocationUtil.isDomain(this.stage, "google.com"));
     * 					trace(LocationUtil.isDomain(this.stage, "bbc.co.uk"));
     * 				</code>
     * 				
     * 				You can even check for subdomains:
     * 				<code>
     * 					trace(LocationUtil.isDomain(this.stage, "subdomain.aaronclinger.com"))
     * 				</code>
     */
    public static function isDomain ( location : DisplayObject, domain : String ) : Boolean {
      return LocationUtil.getDomain(location).slice(-domain.length) == domain;
    }
    /**
     * 			Detects MovieClip's domain location.
     * 			
     * 			@param location: MovieClip to get location of.
     * 			@return Returns full domain (including sub-domains) of MovieClip's location.
     * 			@example
     * 				<code>
     * 					trace(LocationUtil.getDomain(this.stage));
     * 				</code>
     * 			@usageNote Function does not return folder path or file name. The method also treats "www" and sans "www" as the same; if "www" is present method does not return it.
     */
    public static function getDomain ( location : DisplayObject ) : String {
      var baseUrl : String  = location.loaderInfo.url.split('://')[1].split('/')[0];
      return (baseUrl.substr(0, 4) == 'www.') ? baseUrl.substr(4) : baseUrl;
    }
    /**
     * 			Determines if the SWF is running in a browser plug-in.
     * 			
     * 			@return Returns <code>true</code> if SWF is running in the Flash Player browser plug-in; otherwise <code>false</code>.
     */
    public static function isPlugin (  ) : Boolean {
      return Capabilities.playerType == 'PlugIn' || Capabilities.playerType == 'ActiveX';
    }
    /**
     * 			Determines if the SWF is running in the IDE.
     * 			
     * 			@return Returns <code>true</code> if SWF is running in the Flash Player version used by the external player or test movie mode; otherwise <code>false</code>.
     */
    public static function isIde (  ) : Boolean {
      return Capabilities.playerType == 'External';
    }
    /**
     * 			Determines if the SWF is running in the StandAlone player.
     * 			
     * 			@return Returns <code>true</code> if SWF is running in the Flash StandAlone Player; otherwise <code>false</code>.
     */
    public static function isStandAlone (  ) : Boolean {
      return Capabilities.playerType == 'StandAlone';
    }
    /**
     * 			Determines if the runtime environment is an Air application.
     * 			
     * 			@return Returns <code>true</code> if the runtime environment is an Air application; otherwise <code>false</code>.
     */
    public static function isAirApplication (  ) : Boolean {
      return Capabilities.playerType == 'Desktop';
    }
  }
}




