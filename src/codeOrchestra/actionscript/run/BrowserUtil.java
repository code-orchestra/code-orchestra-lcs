package codeOrchestra.actionScript.run;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import codeOrchestra.lcs.logging.Logger;

import com.intellij.openapi.util.SystemInfo;

public class BrowserUtil {

  private static final String FILE = "file";
  private static final String JAR = "jar";
  private static final String MAILTO = "mailto";

  private static final Logger LOG = Logger.getLogger("#" + BrowserUtil.class.getName());

  // The pattern for 'scheme' mainly according to RFC1738.
  // We have to violate the RFC since we need to distinguish
  // real schemes from local Windows paths; The only difference
  // with RFC is that we do not allow schemes with length=1 (in other case
  // local paths like "C:/temp/index.html" whould be erroneously interpreted as
  // external URLs.)
  private static final Pattern ourExternalPrefix = Pattern.compile("^[\\w\\+\\.\\-]{2,}:");
  private static final Pattern ourAnchorsuffix = Pattern.compile("#(.*)$");

  private BrowserUtil() {
  }

  public static boolean isAbsoluteURL(String url) {
    return ourExternalPrefix.matcher(url.toLowerCase()).find();
  }

  public static String getDocURL(String url) {
    Matcher anchorMatcher = ourAnchorsuffix.matcher(url);

    if (anchorMatcher.find()) {
      return anchorMatcher.reset().replaceAll("");
    }

    return url;
  }

  public static URL getURL(String url) throws java.net.MalformedURLException {
    if (!isAbsoluteURL(url)) {
      return new URL("file", "", url);
    }

    return convertToURL(url);
  }

  private static ProcessBuilder launchBrowser(final String url, String[] command) {
    try {
      URL curl = BrowserUtil.getURL(url);

      if (curl != null) {
        final String urlString = curl.toString();
        String[] commandLine;
        if (SystemInfo.isWindows) {
          commandLine = new String[command.length + 2];
          System.arraycopy(command, 0, commandLine, 0, command.length);
          commandLine[commandLine.length - 2] = "\"\"";
          commandLine[commandLine.length - 1] = "\"" + redirectUrl(url, urlString) + "\"";
        } else {
          commandLine = new String[command.length + 1];
          System.arraycopy(command, 0, commandLine, 0, command.length);
          commandLine[commandLine.length - 1] = escapeUrl(urlString);
        }
        return new ProcessBuilder(commandLine);
      } else {
        showErrorMessage("The URL specified is invalid: " + url, "Error");
      }
    } catch (final IOException e) {
      showErrorMessage("Can't start a browser", "Error");
    }
    return null;
  }

  /**
   * This method works around Windows 'start' command behaivor of dropping
   * anchors from the url for local urls.
   */
  private static String redirectUrl(String url, String urlString) throws IOException {
    if (url.indexOf('&') == -1 && (!urlString.startsWith("file:") || urlString.indexOf("#") == -1))
      return urlString;

    File redirect = File.createTempFile("redirect", ".html");
    redirect.deleteOnExit();
    FileWriter writer = new FileWriter(redirect);
    writer.write("<html><head></head><body><script type=\"text/javascript\">window.location=\"" + url + "\";</script></body></html>");
    writer.close();
    return pathToUrl(redirect.getAbsolutePath());
  }

  private static void showErrorMessage(final String message, final String title) {
    MessageDialog dg = new MessageDialog(Display.getCurrent().getActiveShell(), title, null, message, MessageDialog.ERROR, new String[] { IDialogConstants.OK_LABEL }, 0);
    dg.open();
  }

  public static ProcessBuilder launchBrowser(String url, String name) {
    return launchBrowser(url, getDefaultBrowserCommand());
  }

  public static String escapeUrl(String url) {
    if (SystemInfo.isWindows) {
      return "\"" + url + "\"";
    } else {
      return url.replaceAll(" ", "%20");
    }
  }

  public static ProcessBuilder launchBrowser(final String url) {
    return launchBrowser(url, (String) null);
  }

  private static String[] getDefaultBrowserCommand() {
    if (SystemInfo.isWindows9x) {
      return new String[] { "command.com", "/c", "start" };
    } else if (SystemInfo.isWindows) {
      return new String[] { "cmd.exe", "/c", "start" };
    } else if (SystemInfo.isMac) {
      return new String[] { "open" };
    } else if (SystemInfo.isUnix) {
      return new String[] { "mozilla" };
    } else {
      return null;
    }
  }

  public static boolean canStartDefaultBrowser() {
    if (SystemInfo.isMac) {
      return true;
    }

    if (SystemInfo.isWindows) {
      return true;
    }

    return false;
  }

  /**
   * Converts VsfUrl info java.net.URL. Does not support "jar:" protocol.
   * 
   * @param vfsUrl
   *          VFS url (as constructed by VfsFile.getUrl())
   * @return converted URL or null if error has occured
   */

  public static URL convertToURL(String vfsUrl) {
    if (vfsUrl.startsWith(JAR)) {
      LOG.error("jar: protocol not supported.");
      return null;
    }

    // [stathik] for supporting mail URLs in Plugin Manager
    if (vfsUrl.startsWith(MAILTO)) {
      try {
        return new URL(vfsUrl);
      } catch (MalformedURLException e) {
        return null;
      }
    }

    String[] split = vfsUrl.split("://");

    if (split.length != 2) {
      LOG.debug("Malformed VFS URL: " + vfsUrl);
      return null;
    }

    String protocol = split[0];
    String path = split[1];

    try {
      if (protocol.equals(FILE)) {
        return new URL(protocol, "", path);
      } else {
        return new URL(vfsUrl);
      }
    } catch (MalformedURLException e) {
      LOG.debug("MalformedURLException occured:" + e.getMessage());
      return null;
    }
  }

  public static String pathToUrl(String path) {
    return constructUrl("file", path);
  }

  public static String constructUrl(String protocol, String path) {
    return protocol + "://" + path;
  }

}
