package codeOrchestra.utils;

/**
 * @author Alexander Eliseyev
 */
public class StringUtils {

  public static boolean isEmpty(String command) {
    return command == null || command.trim().isEmpty();
  }

  public static String join(final String[] strings, final String separator) {
    return join(strings, 0, strings.length, separator);
  }

  public static String join(final String[] strings, int startIndex, int endIndex, final String separator) {
    final StringBuilder result = new StringBuilder();
    for (int i = startIndex; i < endIndex; i++) {
      if (i > startIndex) result.append(separator);
      result.append(strings[i]);
    }
    return result.toString();
  }
  
}
