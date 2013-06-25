package codeOrchestra.lcs.license;

/**
 * @author Alexander Eliseyev
 */
public final class DemoHelper {
  
  private static DemoHelper demoHelper = new DemoHelper();
  
  public static final DemoHelper get() {
    return demoHelper;
  }
  
  private DemoHelper() {    
  }

  private int compilationsCount = 1;
  
  private static final int MAX_COMPILATIONS_COUNT = 10;
  
  public int getMaxCompilationsCount() {
    return MAX_COMPILATIONS_COUNT;
  }
  
  public void incrementCompilationsCount() {
    compilationsCount++;
  }
  
  public boolean maxCompilationsCountReached() {
    return compilationsCount >= MAX_COMPILATIONS_COUNT;
  }
  
  public boolean isInDemoMode() {
    return ExpirationHelper.getExpirationStrategy().allowsDemo() && ExpirationHelper.getExpirationStrategy().isInDemoMode();
  }
  
}
