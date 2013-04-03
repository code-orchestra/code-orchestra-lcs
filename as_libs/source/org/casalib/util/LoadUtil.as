package org.casalib.util{
  
  import org.casalib.math.Percent;
  
  /**
   * 		Utilities for calculating a load's speed and progress.
   * 		
   * 		@author Aaron Clinger
   * 		@author Mike Creighton
   * 		@version 01/08/09
   */
  public class LoadUtil extends Object {
    /**
     * 			Calculates the load speed in bytes per second (Bps).
     * 			
     * 			@param bytesLoaded: Number of bytes that have loaded between <code>startTime</code> and <code>elapsedTime</code>.
     * 			@param startTime: Time in milliseconds when the load started.
     * 			@param elapsedTime: Time in milliseconds since the load started or time when load completed.
     * 			@return Bytes per second.
     * 			@usageNote This method returns BYTES per second, not bits per second.
     */
    public static function calculateBps ( bytesLoaded : uint, startTime : uint, elapsedTime : uint ) : int {
      return Math.max(0, (bytesLoaded / ConversionUtil.millisecondsToSeconds(elapsedTime - startTime)));
    }
    /**
     * 			Calculates the load speed in kilobytes per second (kBps).
     * 			
     * 			@param bytesLoaded: Number of bytes that have loaded between <code>startTime</code> and <code>elapsedTime</code>.
     * 			@param startTime: Time in milliseconds when the load started.
     * 			@param elapsedTime: Time in milliseconds since the load started or time when load completed.
     * 			@return Kilobytes per second.
     * 			@usageNote This method returns kiloBYTES per second, not kilobits per second.
     */
    public static function calculateKBps ( bytesLoaded : uint, startTime : uint, elapsedTime : uint ) : Number {
      return ConversionUtil.bytesToKilobytes(LoadUtil.calculateBps(bytesLoaded, startTime, elapsedTime));
    }
    /**
     * 			Calculates the percent the video has buffered.
     * 			
     * 			@param bytesLoaded: Number of bytes that have loaded between <code>startTime</code> and <code>elapsedTime</code>.
     * 			@param bytesTotal: Number of bytes total to be loaded.
     * 			@param startTime: Time in milliseconds when the load started.
     * 			@param elapsedTime: The current time in milliseconds or time when load completed.
     * 			@param lengthInMilliseconds: The total duration/length of the video in milliseconds.
     * 			@return The percent buffered.
     */
    public static function calculateBufferPercent ( bytesLoaded : uint, bytesTotal : uint, startTime : uint, elapsedTime : uint, lengthInMilliseconds : uint ) : Percent {
      var totalWait : Number  = bytesTotal / (bytesLoaded / (elapsedTime - startTime)) - lengthInMilliseconds;
      var millisecondsRemaining : uint  = LoadUtil.calculateMillisecondsUntilBuffered(bytesLoaded, bytesTotal, startTime, elapsedTime, lengthInMilliseconds);
      
      return (totalWait == Number.POSITIVE_INFINITY) ? new Percent(0) : new Percent(NumberUtil.constrain(1 - millisecondsRemaining / totalWait, 0, 1));
    }
    /**
     * 			Calculates the remaining time until the video is buffered.
     * 			
     * 			@param bytesLoaded: Number of bytes that have loaded between <code>startTime</code> and <code>elapsedTime</code>.
     * 			@param bytesTotal: Number of bytes total to be loaded.
     * 			@param startTime: Time in milliseconds when the load started.
     * 			@param elapsedTime: The current time in milliseconds or time when load completed.
     * 			@param lengthInMilliseconds: The total duration/length of the video in milliseconds.
     * 			@return The amount millisecond that remain before the video is buffered.
     */
    public static function calculateMillisecondsUntilBuffered ( bytesLoaded : uint, bytesTotal : uint, startTime : uint, elapsedTime : uint, lengthInMilliseconds : uint ) : uint {
      return Math.max(Math.ceil((bytesTotal - bytesLoaded) / (bytesLoaded / (elapsedTime - startTime))) - lengthInMilliseconds, 0);
    }
  }
}




