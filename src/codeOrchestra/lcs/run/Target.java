package codeOrchestra.lcs.run;

public enum Target {
  SWF,
  WEB_ADDRESS,
  AIR,
  AIR_IOS;
  
  public static Target parse(String str) {
    for (Target target : values()) {
      if (target.name().equals(str)) {
        return target;
      }
    }
    return SWF;
  }
  
}
