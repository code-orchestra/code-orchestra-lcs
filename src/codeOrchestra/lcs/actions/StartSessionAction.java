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
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.run.LiveLauncher;
import codeOrchestra.lcs.run.LoggingProcessListener;
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
      LiveCodingProjectViews.saveProjectViewsState(window, currentProject);
      currentProject.save();

      // Validate project
      if (!LiveCodingProjectViews.validateProjectViewsState(window, currentProject)) {
        setEnabled(true);
        return;
      }
    } catch (Throwable t) {
      setEnabled(true);
      throw new RuntimeException(t);
    }

    Job job = new Job("Base Compilation") {
      @Override
      protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Base Compilation", 100);

        // Build digests
        monitor.setTaskName("Building libs digests");
        new ProjectDigestHelper(LCSProject.getCurrentProject()).build();
        monitor.worked(40);

        // Restart FCSH
        monitor.setTaskName("Restaring FCSH");
        try {
          FCSHManager.instance().restart();
        } catch (FCSHException e) {
          // TODO: handle nicely
          e.printStackTrace();
          setEnabled(true);
          return Status.CANCEL_STATUS;
        }
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e1) {
        }
        monitor.worked(10);

        // Base compilation
        monitor.setTaskName("Compiling");
        boolean successfulBaseGeneration = LiveCodingManager.instance().runBaseCompilation();
        monitor.worked(40);

        // Start the compiled SWF
        monitor.setTaskName("Launching");
        if (successfulBaseGeneration) {
          try {
            ProcessHandler processHandler = new LiveLauncher().launch(LCSProject.getCurrentProject());
            processHandler.addProcessListener(new LoggingProcessListener("Launch"));
            processHandler.startNotify();
          } catch (ExecutionException e) {
            // TODO: handle nicely
            e.printStackTrace();
            setEnabled(true);
          }
        }
        monitor.worked(10);

        setEnabled(true);
        return Status.OK_STATUS;
      }
    };
    job.schedule();
  }

}
