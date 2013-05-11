package codeOrchestra.actionScript.compiler.fcsh;

import java.io.IOException;
import java.util.List;

import codeOrchestra.actionScript.compiler.fcsh.console.command.CommandCallback;
import codeOrchestra.actionScript.compiler.fcsh.console.command.FCSHCommandExecuteThread;
import codeOrchestra.actionScript.compiler.fcsh.console.command.FCSHCommandRunnable;
import codeOrchestra.actionScript.compiler.fcsh.console.command.impl.COMPCCommand;
import codeOrchestra.actionScript.compiler.fcsh.console.command.impl.CPUProfilingStartCommand;
import codeOrchestra.actionScript.compiler.fcsh.console.command.impl.CPUProfilingStopCommand;
import codeOrchestra.actionScript.compiler.fcsh.console.command.impl.LivecodingBaseCOMPCCommand;
import codeOrchestra.actionScript.compiler.fcsh.console.command.impl.LivecodingBaseMXMLCCommand;
import codeOrchestra.actionScript.compiler.fcsh.console.command.impl.LivecodingCachesDeleteCommand;
import codeOrchestra.actionScript.compiler.fcsh.console.command.impl.LivecodingIncrementalCOMPCCommand;
import codeOrchestra.actionScript.modulemaker.CompilationResult;
import codeOrchestra.lcs.fcsh.FCSHProcessHandler;
import codeOrchestra.lcs.logging.Logger;

/**
 * @author Alexander Eliseyev
 */
public class FCSHManager {

  private static FCSHManager instance = new FCSHManager();
  
  public static FCSHManager instance() {
    return instance;
  }
  
  public static final Logger LOG = Logger.getLogger("fcsh");

  private static final int FCSH_INIT_CHECK_INTERVAL = 100;
  public static final long FCSH_INIT_TIMEOUT = 3000;

  private FCSHProcessHandler fcshProcessHandler;

  public void restart() throws FCSHException {
    destroyProcess();
    assureFCSHIsActive();
  }

  public void destroyProcess() {
    try {
      if (fcshProcessHandler != null && !fcshProcessHandler.isProcessTerminated()) {
        fcshProcessHandler.destroyProcess();
      }
    } catch (Throwable t) {
      // ignore
    }
  }

  private void assureFCSHIsActive() throws FCSHException {
    if (fcshProcessHandler != null && !fcshProcessHandler.isProcessTerminated()) {
      // No need to reactivate, the process is still running
      return;
    }

    FCSHLauncher fcshLauncher = new FCSHLauncher();
    ProcessBuilder processBuilder = fcshLauncher.createProcessBuilder();
    Process fcshProcess;
    try {
      fcshProcess = processBuilder.start();
    } catch (IOException e) {
      throw new FCSHException("Error while trying to start the fcsh process", e);
    }

    LOG.info(fcshLauncher.getCommandString());
    
    fcshProcessHandler = new FCSHProcessHandler(fcshProcess, fcshLauncher.getCommandString());
    fcshProcessHandler.startNotify();

    // Give fcsh some time to start up
    long timeout = FCSH_INIT_TIMEOUT;
    while (!fcshProcessHandler.isInitialized()) {
      if (timeout < 0) {
        return;
      }

      try {
        Thread.sleep(FCSH_INIT_CHECK_INTERVAL);
        timeout -= FCSH_INIT_CHECK_INTERVAL;
      } catch (InterruptedException e) {
        // ignore
      }
    }
  }

  public void submitCommand(CommandCallback commandCallback) throws FCSHException {
    assureFCSHIsActive();

    FCSHCommandRunnable fcshCommandRunnable = new FCSHCommandRunnable(this, commandCallback);
    if (commandCallback.isSynchronous()) {
      fcshCommandRunnable.run();
    } else {
      new FCSHCommandExecuteThread(fcshCommandRunnable).start();
    }
  }

  public CompilationResult baseMXMLC(List<String> arguments) throws FCSHException {
    assureFCSHIsActive();

    LivecodingBaseMXMLCCommand mxmlcCommand = new LivecodingBaseMXMLCCommand(arguments);
    LOG.info("Compiling: " + mxmlcCommand.getCommand());

    submitCommand(mxmlcCommand);

    return mxmlcCommand.getCompileResult();
  }

  public CompilationResult baseCOMPC(List<String> arguments) throws FCSHException {
    assureFCSHIsActive();

    LivecodingBaseCOMPCCommand compcCommand = new LivecodingBaseCOMPCCommand(arguments);
    LOG.info("Compiling: " + compcCommand.getCommand());

    submitCommand(compcCommand);

    return compcCommand.getCompileResult();
  }
  
  public CompilationResult incrementalCOMPC(List<String> arguments) throws FCSHException {
    assureFCSHIsActive();

    LivecodingIncrementalCOMPCCommand compcCommand = new LivecodingIncrementalCOMPCCommand(arguments);
    LOG.info("Compiling: " + compcCommand.getCommand());

    submitCommand(compcCommand);

    return compcCommand.getCompileResult();
  }
  
  public CompilationResult compc(List<String> commandArguments) throws FCSHException {
    assureFCSHIsActive();

    COMPCCommand compcCommand = new COMPCCommand(commandArguments);
    LOG.info("Compiling: " + compcCommand.getCommand());

    submitCommand(compcCommand);

    return compcCommand.getCompileResult();
  }

  public void deleteLivecodingCaches() throws FCSHException {
    assureFCSHIsActive();

    LivecodingCachesDeleteCommand deleteCachesCommand = new LivecodingCachesDeleteCommand();
    submitCommand(deleteCachesCommand);
  }
  
  public void startCPUProfiling() throws FCSHException {
    if (!FCSHLauncher.PROFILING_ON) {
      return;
    }
    assureFCSHIsActive();
    submitCommand(new CPUProfilingStartCommand());
  }
  
  public void stopCPUProfiling() throws FCSHException {
    if (!FCSHLauncher.PROFILING_ON) {
      return;
    }
    assureFCSHIsActive();
    submitCommand(new CPUProfilingStopCommand());
  }

  public FCSHProcessHandler getProcessHandler() {
    return fcshProcessHandler;
  }

}
