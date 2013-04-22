package codeOrchestra.lcs.run;

import codeOrchestra.lcs.logging.Logger;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;

public class LoggingProcessListener extends ProcessAdapter {

  private Logger logger;
  
  public LoggingProcessListener(String loggerName) {
    logger = Logger.getLogger(loggerName);
  }
  
  @Override
  public void onTextAvailable(ProcessEvent event, String outputType) {                
    String text = event.getText().trim();
    
    if ("finished.with.exit.code.text.message".equals(text)) {
      logger.info("Finished with exit code " + event.getExitCode());
      return;
    }
    
    if ("SYSTEM".equals(outputType)) {
      logger.info(text);
    } else if ("STDERR".equals(outputType)) {
      logger.error(text);                  
    } else {
      logger.info(text);      
    }
  }
  
}
