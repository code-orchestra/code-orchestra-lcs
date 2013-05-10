package codeOrchestra.lcs.flex.usedCode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import codeOrchestra.utils.XMLUtils;

public class LinkReportReader {

  private File reportFile;

  public LinkReportReader(File reportFile) {
    this.reportFile = reportFile;
  }
  
  public List<String> fetchUsedClassesFqNames() {
    List<String> result = new ArrayList<String>();
    
    Document document = XMLUtils.fileToDOM(reportFile);
    Element documentElement = document.getDocumentElement();
    if (documentElement != null) {
      NodeList scriptsElements = document.getElementsByTagName("scripts");
      if (scriptsElements != null) {
        for (int i = 0; i < scriptsElements.getLength(); i++) {
          Element scriptsElement = (Element) scriptsElements.item(i);
          
          NodeList scriptElements = scriptsElement.getElementsByTagName("script");
          if (scriptElements != null) {
            for (int j = 0; j < scriptElements.getLength(); j++) {
              Element scriptElement = (Element) scriptElements.item(j);
              
              NodeList defElements = scriptElement.getElementsByTagName("def");
              if (defElements != null && defElements.getLength() > 0) {
                Element defElement = (Element) defElements.item(0);
                String idAttribute = defElement.getAttribute("id");
                if (idAttribute != null) {
                  String fqName;                  
                  if (idAttribute.contains(":")) {
                    fqName = idAttribute.replace(":", ".");
                  } else {
                    fqName = idAttribute;
                  }
                  
                  if (!result.contains(fqName)) {
                    result.add(fqName);
                  }
                }
              }
            }
          }
        }
      }
    }
    
    return result;
  }
  
}
