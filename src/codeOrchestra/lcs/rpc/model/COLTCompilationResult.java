package codeOrchestra.lcs.rpc.model;

import java.util.ArrayList;
import java.util.List;

import codeOrchestra.actionScript.modulemaker.CompilationResult;
import codeOrchestra.actionScript.modulemaker.messages.CompilerMessage;

/**
 * @author Alexander Eliseyev
 */
public class COLTCompilationResult {
  
  private boolean successful;
  private COLTCompilerMessage[] errorMessages;
  private COLTCompilerMessage[] warningMessages;
  
  public COLTCompilationResult() {    
  }
  
  public COLTCompilationResult(CompilationResult compilationResult) {
    this.successful = compilationResult.isOk();
    
    List<COLTCompilerMessage> errorMessagesList = new ArrayList<COLTCompilerMessage>();
    List<COLTCompilerMessage> warningMessagesList = new ArrayList<COLTCompilerMessage>();
    
    List<CompilerMessage> messages = compilationResult.getMessages();
    for (CompilerMessage compilerMessage : messages) {
      switch (compilerMessage.getType()) {
      case ERROR:
        errorMessagesList.add(new COLTCompilerMessage(compilerMessage));
        break;
      case WARNING:
        warningMessagesList.add(new COLTCompilerMessage(compilerMessage));
        break;
      default:
        break;
      }
    }
    
    errorMessages = errorMessagesList.toArray(new COLTCompilerMessage[errorMessagesList.size()]);
    warningMessages = warningMessagesList.toArray(new COLTCompilerMessage[warningMessagesList.size()]);
  }

  public boolean isSuccessful() {
    return successful;
  }

  public void setSuccessful(boolean successful) {
    this.successful = successful;
  }

  public COLTCompilerMessage[] getErrorMessages() {
    return errorMessages;
  }

  public void setErrorMessages(COLTCompilerMessage[] errorMessages) {
    this.errorMessages = errorMessages;
  }

  public COLTCompilerMessage[] getWarningMessages() {
    return warningMessages;
  }

  public void setWarningMessages(COLTCompilerMessage[] warningMessages) {
    this.warningMessages = warningMessages;
  }

  
}
