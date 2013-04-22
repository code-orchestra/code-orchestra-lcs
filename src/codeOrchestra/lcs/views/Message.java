package codeOrchestra.lcs.views;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import codeOrchestra.lcs.logging.Level;

public class Message {

  private long timestamp;
  private String source;
  private Level level;
  private String message;
  private String stackTrace;
  
  public Message(String source, Level level, String message, long timestamp, String stackTrace) {
    this.source = source;
    this.level = level;
    this.message = message;
    this.timestamp = timestamp;
    this.stackTrace = stackTrace;
  }
  
  public Level getLevel() {
    return level;
  }

  @SuppressWarnings("deprecation")
  public String getCreationTimeString(long timestamp) {
    Date date = new Date(timestamp);
    return expand("" + date.getHours(), 2) + ":" +
      expand("" + date.getMinutes(), 2) + ":" + expand("" + date.getSeconds(), 2);
  }
  
  private String expand(String s, int n) {
    for (int i = 0; i < n - s.length(); i++) {
      s = "0" + s;
    }
    return s;
  }
  
  public TableItem createTableItem(Table parent, boolean showSource) {
    TableItem tableItem = new TableItem(parent, SWT.NONE);
    
    tableItem.setImage(level.getImage());
    tableItem.setText(getMessageText(showSource));
    
    return tableItem;
  }

  public String getMessageText(boolean showSource) {
    return getCreationTimeString(timestamp) + (showSource ? " : [" + source + "] " : " ") +  message;
  }

  public String getStackTrace() {
    return stackTrace;
  }


}
