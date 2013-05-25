package codeOrchestra.lcs.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;

import codeOrchestra.actionScript.compiler.fcsh.FCSHException;
import codeOrchestra.actionScript.compiler.fcsh.FCSHManager;
import codeOrchestra.lcs.ICommandIds;
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

public class StartSessionAction extends Action {

  private final IWorkbenchWindow window;

  public StartSessionAction(IWorkbenchWindow window) {
    this.window = window;
    setText("Start Live Coding Session");
    setId(ICommandIds.CMD_START_SESSION);
    setActionDefinitionId(ICommandIds.CMD_START_SESSION);
    setImageDescriptor(codeOrchestra.lcs.Activator.getImageDescriptor("/icons/run.png"));

    setEnabled(false);

    window.addPerspectiveListener(new PerspectiveAdapter() {
      @Override
      public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
        setEnabled(LCSProject.getCurrentProject() != null);
      }
    });
  }

  @Override
  public void run() {
    setEnabled(false);
    
    try {
      // Save project
      final LCSProject currentProject = LCSProject.getCurrentProject();
      if (!LiveCodingProjectViews.saveProjectViewsState(window, currentProject)) {
        setEnabled(true);
        return;
      }
      currentProject.save();

      // Clear messages
      if (currentProject.getLiveCodingSettings().clearMessagesOnSessionStart()) {
        MessagesManager.getInstance().clear();
      }
    } catch (Throwable t) {
      setEnabled(true);
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
          setEnabled(true);
          return Status.CANCEL_STATUS;
        }
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e1) {
        }
        monitor.worked(10);

        // Base compilation
        report(monitor, "Compiling");        
        boolean successfulBaseGeneration = LiveCodingManager.instance().runBaseCompilation();
        monitor.worked(40);
        
        if (successfulBaseGeneration) {
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
            e.printStackTrace();
            setEnabled(true);
            return Status.CANCEL_STATUS;
          }
        } else {
          setEnabled(true);
          return Status.CANCEL_STATUS;          
        }

        setEnabled(true);
        return Status.OK_STATUS;
      }
    };
    job.schedule();
  }

}
