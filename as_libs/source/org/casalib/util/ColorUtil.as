package org.casalib.util{
  
  import flash.geom.ColorTransform;
  import org.casalib.math.Percent;
  
  /**
   * 		Provides utility functions for dealing with color.
   * 		
   * 		@author Aaron Clinger
   * 		@version 03/29/10
   */
  public class ColorUtil extends Object {
    /**
     * 			Interpolates (tints) between two colors.
     * 			
     * 			@param begin: The start color.
     * 			@param end: The finish color.
     * 			@param amount: The level of interpolation between the two colors.
     * 			@return The new interpolated color.
     * 			@example
     * 				<code>
     * 					var myColor:ColorTransform = new ColorTransform();
     * 					myColor.color              = 0xFF0000;
     * 					
     * 					var box:Sprite = new Sprite();
     * 					box.graphics.beginFill(0x0000FF);
     * 					box.graphics.drawRect(10, 10, 250, 250);
     * 					box.graphics.endFill();
     * 					
     * 					box.transform.colorTransform = ColorUtil.interpolateColor(new ColorTransform(), myColor, new Percent(0.5));
     * 					
     * 					this.addChild(box);
     * 				</code>
     */
    public static function interpolateColor ( begin : ColorTransform, end : ColorTransform, amount : Percent ) : ColorTransform {
      var interpolation : ColorTransform  = new ColorTransform();
      
      interpolation.redMultiplier = NumberUtil.interpolate(amount, begin.redMultiplier, end.redMultiplier);
      interpolation.greenMultiplier = NumberUtil.interpolate(amount, begin.greenMultiplier, end.greenMultiplier);
      interpolation.blueMultiplier = NumberUtil.interpolate(amount, begin.blueMultiplier, end.blueMultiplier);
      interpolation.alphaMultiplier = NumberUtil.interpolate(amount, begin.alphaMultiplier, end.alphaMultiplier);
      interpolation.redOffset = NumberUtil.interpolate(amount, begin.redOffset, end.redOffset);
      interpolation.greenOffset = NumberUtil.interpolate(amount, begin.greenOffset, end.greenOffset);
      interpolation.blueOffset = NumberUtil.interpolate(amount, begin.blueOffset, end.blueOffset);
      interpolation.alphaOffset = NumberUtil.interpolate(amount, begin.alphaOffset, end.alphaOffset);
      
      return interpolation;
    }
    /**
     * 			Converts a series of individual RGB(A) values to a 32-bit ARGB color value.
     * 			
     * 			@param r: A uint from 0 to 255 representing the red color value.
     * 			@param g: A uint from 0 to 255 representing the green color value.
     * 			@param b: A uint from 0 to 255 representing the blue color value.
     * 			@param a: A uint from 0 to 255 representing the alpha value. Default is <code>255</code>.
     * 			@return Returns a hexidecimal color as a String.
     * 			@example
     * 				<code>
     * 					var hexColor : String = ColorUtil.getHexStringFromARGB(128, 255, 0, 255);
     * 					trace(hexColor); // Traces 80FF00FF
     * 				</code>
     */
    public static function getColor ( r : uint, g : uint, b : uint, a : uint  = 255 ) : uint {
      return (a << 24) | (r << 16) | (g << 8) | b;
    }
    /**
     * 			Converts a 32-bit ARGB color value into an ARGB object.
     * 			
     * 			@param color: The 32-bit ARGB color value.
     * 			@return Returns an object with the properties a, r, g, and b defined.
     * 			@example
     * 				<code>
     * 					var myRGB:Object = ColorUtil.getARGB(0xCCFF00FF);
     * 					trace("Alpha = " + myRGB.a);
     * 					trace("Red = " + myRGB.r);
     * 					trace("Green = " + myRGB.g);
     * 					trace("Blue = " + myRGB.b);
     * 				</code>
     */
    public static function getARGB ( color : uint ) : Object {
      var c : Object  = {};
      c.a = color >> 24 & 0xFF;
      c.r = color >> 16 & 0xFF;
      c.g = color >> 8 & 0xFF;
      c.b = color & 0xFF;
      return c;
    }
    /**
     * 			Converts a 24-bit RGB color value into an RGB object.
     * 			
     * 			@param color: The 24-bit RGB color value.
     * 			@return Returns an object with the properties r, g, and b defined.
     * 			@example
     * 				<code>
     * 					var myRGB:Object = ColorUtil.getRGB(0xFF00FF);
     * 					trace("Red = " + myRGB.r);
     * 					trace("Green = " + myRGB.g);
     * 					trace("Blue = " + myRGB.b);
     * 				</code>
     */
    public static function getRGB ( color : uint ) : Object {
      var c : Object  = {};
      c.r = color >> 16 & 0xFF;
      c.g = color >> 8 & 0xFF;
      c.b = color & 0xFF;
      return c;
    }
    /**
     * 			Converts a 32-bit ARGB color value into a hexidecimal String representation.
     * 			
     * 			@param a: A uint from 0 to 255 representing the alpha value.
     * 			@param r: A uint from 0 to 255 representing the red color value.
     * 			@param g: A uint from 0 to 255 representing the green color value.
     * 			@param b: A uint from 0 to 255 representing the blue color value.
     * 			@return Returns a hexidecimal color as a String.
     * 			@example
     * 				<code>
     * 					var hexColor : String = ColorUtil.getHexStringFromARGB(128, 255, 0, 255);
     * 					trace(hexColor); // Traces 80FF00FF
     * 				</code>
     */
    public static function getHexStringFromARGB ( a : uint, r : uint, g : uint, b : uint ) : String {
      var aa : String  = a.toString(16);
      var rr : String  = r.toString(16);
      var gg : String  = g.toString(16);
      var bb : String  = b.toString(16);
      aa = (aa.length == 1) ? '0' + aa : aa;
      rr = (rr.length == 1) ? '0' + rr : rr;
      gg = (gg.length == 1) ? '0' + gg : gg;
      bb = (bb.length == 1) ? '0' + bb : bb;
      return (aa + rr + gg + bb).toUpperCase();
    }
    /**
     * 			Converts an RGB color value into a hexidecimal String representation.
     * 			
     * 			@param r: A uint from 0 to 255 representing the red color value.
     * 			@param g: A uint from 0 to 255 representing the green color value.
     * 			@param b: A uint from 0 to 255 representing the blue color value.
     * 			@return Returns a hexidecimal color as a String.
     * 			@example
     * 				<code>
     * 					var hexColor : String = ColorUtil.getHexStringFromRGB(255, 0, 255);
     * 					trace(hexColor); // Traces FF00FF
     * 				</code>
     */
    public static function getHexStringFromRGB ( r : uint, g : uint, b : uint ) : String {
      var rr : String  = r.toString(16);
      var gg : String  = g.toString(16);
      var bb : String  = b.toString(16);
      rr = (rr.length == 1) ? '0' + rr : rr;
      gg = (gg.length == 1) ? '0' + gg : gg;
      bb = (bb.length == 1) ? '0' + bb : bb;
      return (rr + gg + bb).toUpperCase();
    }
  }
}




