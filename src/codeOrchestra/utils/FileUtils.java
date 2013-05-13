package codeOrchestra.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import jetbrains.mps.util.ReadUtil;

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

  public static String unixify(String path) {
    return path.replace("\\", "/");
  }
  
  public static boolean clear(File dir) {
    File[] files = dir.listFiles();
    if (files == null) return true;

    boolean result = true;

    for (File f : files) {
      boolean r = delete(f);
      result = result && r;
    }

    return result;
  }
  
  public static boolean delete(File root) {
    boolean result = true;

    if (root.isDirectory()) {
      for (File child : root.listFiles()) {
        result = delete(child) && result;
      }
    }
    // !result means one of children was not deleted, hence you may not delete this directory
    return result && root.delete();
  }
  
  public static File getTempDir() {
    return new File(System.getProperty("java.io.tmpdir"));
  }
  
  public static void copyFileChecked(File f, File to, boolean checkEquals) throws IOException {
    File target;
    if (to.isDirectory()) {
      target = new File(to, f.getName());
    } else {
      target = to;
    }

    if (!to.getParentFile().exists()) {
      to.getParentFile().mkdirs();
    }

    if (checkEquals && target.exists()) {
      String existingContents = FileUtils.read(target);
      if (existingContents.equals(FileUtils.read(f))) {
        return;
      }
    }

    byte[] bytes = new byte[(int) f.length()];

    OutputStream os = new FileOutputStream(target);
    FileInputStream is = new FileInputStream(f);

    ReadUtil.read(bytes, is);
    os.write(bytes);

    is.close();
    os.close();
  }
  
  public static String read(File file) {
    try {
      return read(new FileReader(file));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String read(Reader reader) {
    BufferedReader r = null;
    try {
      r = new BufferedReader(reader);

      StringBuilder result = new StringBuilder();

      String line = null;
      while ((line = r.readLine()) != null) {
        result.append(line).append("\n");
      }

      return result.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        if (r != null) {
          r.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
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
