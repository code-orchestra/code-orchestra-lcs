package codeOrchestra.utils;

public final class NameUtil {

  public static String shortNameFromLongName(String fqName) {
    if (fqName == null) return fqName;
    int offset = fqName.lastIndexOf('.');
    if (offset < 0) return fqName;

    return fqName.substring(offset + 1);
  }

}
