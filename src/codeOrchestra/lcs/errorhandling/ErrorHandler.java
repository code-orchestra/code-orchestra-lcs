package codeOrchestra.lcs.errorhandling;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;

import codeOrchestra.lcs.Activator;

public class ErrorHandler {

  public static void handle(final Throwable t, final String message) {
    Display.getDefault().asyncExec(new Runnable() {      
      @Override
      public void run() {
        StringWriter s = new StringWriter();
        t.printStackTrace(new PrintWriter(s));
        
        IStatus status = new Status(IStatus.ERROR, "code-orchestra-lcs", 0, null, t);
        
        ErrorDialog.openError(Display.getCurrent().getActiveShell(), "Error", message, status);    
        Platform.getLog(Activator.getDefault().getBundle()).log(status);
      }
    });    
  }
  
  public static void handle(final String message) {
    Display.getDefault().asyncExec(new Runnable() {      
      @Override
      public void run() {
        IStatus status = new Status(IStatus.ERROR, "code-orchestra-lcs", 0, null, null);
        
        ErrorDialog.openError(Display.getCurrent().getActiveShell(), "Error", message, status);    
        Platform.getLog(Activator.getDefault().getBundle()).log(status);
      }
    });    
  }
  
}
