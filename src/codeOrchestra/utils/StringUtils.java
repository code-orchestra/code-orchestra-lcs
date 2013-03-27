package codeOrchestra.utils;

import java.util.Iterator;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public class StringUtils {

  public static abstract class ILeftCombinator {
    public ILeftCombinator() {
    }

    public abstract String combine(String s, String t);

    public String invoke(String s, String t) {
      return combine(s, t);
    }
  }
  
  public static String foldLeft(Iterable<String> seq, String seed, ILeftCombinator combinator) {
    String s = seed;
    for (Iterator<String> it = seq.iterator(); it.hasNext();) {
      s = combinator.invoke(s, it.next());
    }
    return s;
  }
  
  /**
   * Equivalent to testee.startsWith(firstPrefix + secondPrefix) but avoids creating an object for concatenation.
   *
   * @param testee
   * @param firstPrefix
   * @param secondPrefix
   * @return
   */
  public static boolean startsWithConcatenationOf(String testee, String firstPrefix, String secondPrefix) {
    int l1 = firstPrefix.length();
    int l2 = secondPrefix.length();
    if (testee.length() < l1 + l2) return false;
    return testee.startsWith(firstPrefix) && testee.regionMatches(l1, secondPrefix, 0, l2);
  }
  
  public static boolean isEmpty(String command) {
    return command == null || command.trim().isEmpty();
  }

  public static String join(final List<String> strings, final String separator) {
    return join(strings.toArray(new String[strings.size()]), separator);
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

  public static boolean isNotEmpty(String str) {
    return str != null && str.trim().length() > 0;
  }
  
}
