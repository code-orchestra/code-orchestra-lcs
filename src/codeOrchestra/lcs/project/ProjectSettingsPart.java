package codeOrchestra.lcs.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Alexander Eliseyev
 */
public abstract class ProjectSettingsPart {

  private IPreferenceStore preferenceStore;

  public ProjectSettingsPart(IPreferenceStore preferenceStore) {
    this.preferenceStore = preferenceStore;
  }

  public IPreferenceStore getPreferenceStore() {
    return preferenceStore;
  }

  protected List<String> getPaths(String preferenceName) {
    List<String> result = new ArrayList<String>();    
    
    String pathsEncoded = getPreferenceStore().getString(preferenceName);
    if (pathsEncoded == null) {
      return result;
    }
    
    for (String path : parsePathsString(pathsEncoded)) {
      result.add(path);
    }
    
    return result;
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private String[] parsePathsString(String stringList) {
    StringTokenizer st = new StringTokenizer(stringList, File.pathSeparator + "\n\r");//$NON-NLS-1$
    ArrayList v = new ArrayList();
    while (st.hasMoreElements()) {
      v.add(st.nextElement());
    }
    return (String[]) v.toArray(new String[v.size()]);
  }

}
