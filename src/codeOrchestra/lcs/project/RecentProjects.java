package codeOrchestra.lcs.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.ui.preferences.ScopedPreferenceStore;

import codeOrchestra.lcs.Activator;
import codeOrchestra.utils.StringUtils;

public class RecentProjects {

  private static final String RECENT_COLT_PROJECTS = "recentCOLTProjects";
  
  private static ScopedPreferenceStore store = (ScopedPreferenceStore) Activator.getDefault().getPreferenceStore();

  public static void addRecentProject(String path) {
    List<String> paths = getRecentProjectsPaths();
    if (!paths.contains(path)) {
      paths.add(0, path);
    }
    
    store.setValue(RECENT_COLT_PROJECTS, createList(paths));
    try {
      store.save();
    } catch (IOException e) {
      // ignore
    }
  }

  public static List<String> getRecentProjectsPaths() {
    return parseString(store.getString(RECENT_COLT_PROJECTS));
  }

  private static String createList(List<String> list) {
    StringBuffer path = new StringBuffer("");//$NON-NLS-1$

    for (String item : list) {
      path.append(item);
      path.append(File.pathSeparator);
    }

    return path.toString();
  }

  private static List<String> parseString(String stringList) {
    List<String> result = new ArrayList<String>();
    
    if (StringUtils.isEmpty(stringList)) {
      return result;
    }
    
    StringTokenizer st = new StringTokenizer(stringList, File.pathSeparator + "\n\r");
    
    while (st.hasMoreElements()) {
      result.add((String) st.nextElement());
    }
    return result;
  }

}
