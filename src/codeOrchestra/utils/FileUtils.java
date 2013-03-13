package codeOrchestra.utils;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Alexander Eliseyev
 */
public class FileUtils {
  
  public static final FileFilter DIRECTORY_FILTER = new FileFilter() {
    public boolean accept(File file) {
      return file.isDirectory();
    }
  };

  
  public static boolean doesExist(String path) {
    return new File(path).exists();
  }

}
