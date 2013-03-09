package codeOrchestra.utils;

/**
 * @author Alexander Eliseyev
 */
public class StringUtils {

  public static boolean isEmpty(String command) {
    return command == null || command.trim().isEmpty();
  }

}
