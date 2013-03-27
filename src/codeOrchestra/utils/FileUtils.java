package codeOrchestra.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Alexander Eliseyev
 */
public class FileUtils {
  
  public static final FileFilter FILES_ONLY_FILTER = new FileFilter() {
	  public boolean accept(File file) {
	      return file.isFile();
	  } 
  };
	
  public static final FileFilter DIRECTORY_FILTER = new FileFilter() {
    public boolean accept(File file) {
      return file.isDirectory();
    }
  };

  public static void write(File file, String content) {
    try {
      Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
      out.write(content);
      out.flush();
      out.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static List<File> listFileRecursively(File dir, FileFilter fileFilter) {
    assert dir.isDirectory();
    List<File> files = new ArrayList<File>();

    File[] subdirs = dir.listFiles(new FileFilter() {
      public boolean accept(File f) {
        return f.isDirectory() && !(".svn".equals(f.getName()));
      }
    });

    if (subdirs != null) {
      for (File subdir : subdirs) {
        files.addAll(listFileRecursively(subdir, fileFilter));
      }
      addArrayToList(files, dir.listFiles(fileFilter));
    }

    return files;
  }
  
  public static boolean doesExist(String path) {
    return new File(path).exists();
  }
  
  public static String getRelativePath(String targetPath, String basePath, String pathSeparator) {
    String[] base = basePath.split(Pattern.quote(pathSeparator));
    String[] target = targetPath.split(Pattern.quote(pathSeparator));

    StringBuffer common = new StringBuffer();

    int commonIndex = 0;
    while (commonIndex < target.length && commonIndex < base.length
      && target[commonIndex].equals(base[commonIndex])) {
      common.append(target[commonIndex]).append(pathSeparator);
      commonIndex++;
    }

    if (commonIndex == 0) {
      return null;
    }

    if (target.length == commonIndex && base.length == target.length) {
      return "";
    }

    boolean baseIsFile = true;

    File baseResource = new File(basePath);

    if (baseResource.exists()) {
      baseIsFile = baseResource.isFile();

    } else if (basePath.endsWith(pathSeparator)) {
      baseIsFile = false;
    }

    StringBuffer relative = new StringBuffer();

    if (base.length != commonIndex) {
      int numDirsUp = baseIsFile ? base.length - commonIndex - 1 : base.length - commonIndex;

      for (int i = 0; i < numDirsUp; i++) {
        relative.append("..").append(pathSeparator);
      }
    }
    relative.append(targetPath.substring(common.length()));
    return relative.toString();
  }

  private static void addArrayToList(List<File> list, File[] array) {
    for (File file : array) {
      list.add(file);
    }
  }
  
}
