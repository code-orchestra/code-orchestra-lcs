package codeOrchestra.lcs.controller;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchWindow;

import codeOrchestra.actionScript.compiler.fcsh.FCSHException;
import codeOrchestra.actionScript.compiler.fcsh.FCSHManager;
import codeOrchestra.actionScript.modulemaker.CompilationResult;
import codeOrchestra.lcs.config.view.LiveCodingProjectViews;
import codeOrchestra.lcs.digest.ProjectDigestHelper;
import codeOrchestra.lcs.errorhandling.ErrorHandler;
import codeOrchestra.lcs.messages.MessagesManager;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.run.LiveLauncher;
import codeOrchestra.lcs.run.LoggingProcessListener;
import codeOrchestra.lcs.run.ProcessHandlerWrapper;
import codeOrchestra.lcs.session.LiveCodingManager;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessHandler;

/**
 * @author Alexander Eliseyev
 */
public class COLTController {
  
  
  public static void startBaseCompilationAndRun(IWorkbenchWindow window, final COLTControllerCallback<CompilationResult, CompilationResult> callback, boolean sync) {
    try {
      // Save project
      final LCSProject currentProject = LCSProject.getCurrentProject();
      if (!LiveCodingProjectViews.saveProjectViewsState(window, currentProject)) {
        callback.onError(null, null);
        return;
      }
      currentProject.save();

      // Clear messages
      if (currentProject.getLiveCodingSettings().clearMessagesOnSessionStart()) {
        MessagesManager.getInstance().clear();
      }
    } catch (Throwable t) {
      callback.onError(t, null);
      throw new RuntimeException(t);
    }

    Job job = new Job("Base Compilation") {
      
      private void report(IProgressMonitor monitor, String text) {
        monitor.setTaskName(text);
        setName(text);
      }
      
      @Override
      protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Base Compilation", 100);

        // Build digests
        report(monitor, "Building digests");        
        ProjectDigestHelper projectDigestHelper = new ProjectDigestHelper(LCSProject.getCurrentProject());
        projectDigestHelper.build();
        monitor.worked(30);

        // Restart FCSH
        report(monitor, "Restaring FCSH");        
        try {
          FCSHManager.instance().restart();
        } catch (FCSHException e) {
          ErrorHandler.handle(e, "Error while starting fcsh");
          callback.onError(e, null);
          return Status.CANCEL_STATUS;
        }
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e1) {
        }
        monitor.worked(10);

        // Base compilation
        report(monitor, "Compiling");        
        CompilationResult compilationResult = LiveCodingManager.instance().runBaseCompilation();
        monitor.worked(40);
        
        if (compilationResult.isOk()) {
          // Fetch the embed digest 
          report(monitor, "Reading embed digests");
          LiveCodingManager.instance().resetEmbeds(projectDigestHelper.getEmbedDigests());
          monitor.worked(10);
        
          // Start the compiled SWF
          report(monitor, "Launching");
          try {
            ProcessHandlerWrapper processHandlerWrapper = new LiveLauncher().launch(LCSProject.getCurrentProject());
            ProcessHandler processHandler = processHandlerWrapper.getProcessHandler();
            processHandler.addProcessListener(new LoggingProcessListener("Launch"));
            processHandler.startNotify();
            
            if (processHandlerWrapper.mustWaitForExecutionEnd()) {
              processHandler.waitFor();
            }
            
            monitor.worked(10);
          } catch (ExecutionException e) {
            ErrorHandler.handle(e, "Error while launching build artifact");
            callback.onError(e, compilationResult);
            return Status.CANCEL_STATUS;
          }
        } else {
          callback.onError(null, compilationResult);
          return Status.CANCEL_STATUS;          
        }

        callback.onComplete(compilationResult);
        return Status.OK_STATUS;
      }
    };
    
    job.schedule();
    
    if (sync) {
      try {
        job.join();
      } catch (InterruptedException e) {
        // ignore
      }
    }
  }

}
