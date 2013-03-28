package codeOrchestra.actionScript.compiler.fcsh;

import codeOrchestra.actionScript.compiler.fcsh.console.command.CommandCallback;
import codeOrchestra.actionScript.compiler.fcsh.console.command.FCSHCommandExecuteThread;
import codeOrchestra.actionScript.compiler.fcsh.console.command.FCSHCommandRunnable;
import codeOrchestra.actionScript.compiler.fcsh.console.command.impl.COMPCCommand;
import codeOrchestra.actionScript.compiler.fcsh.console.command.impl.ClearCommand;
import codeOrchestra.actionScript.compiler.fcsh.console.command.impl.CompileTargetCommand;
import codeOrchestra.actionScript.compiler.fcsh.console.command.impl.MXMLCCommand;
import codeOrchestra.actionScript.compiler.fcsh.target.CompilerTarget;
import codeOrchestra.actionScript.modulemaker.CompilationResult;
import codeOrchestra.lcs.fcsh.FCSHProcessHandler;
import codeOrchestra.lcs.logging.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: destroyProcess on app close
 * 
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
  private Map<List<String>, CompilerTarget> compilerTargets = Collections.synchronizedMap(new HashMap<List<String>, CompilerTarget>());
  private boolean livecodingMode;

  public void restart() throws FCSHException {
    destroyProcess();
    assureFCSHIsActive();
  }

  public boolean isInIncrementalLivecodingMode() {
    return livecodingMode;
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

    clearTargets();

    FCSHLauncher fcshLauncher = new FCSHLauncher(livecodingMode);
    ProcessBuilder processBuilder = fcshLauncher.createProcessBuilder();
    Process fcshProcess;
    try {
      fcshProcess = processBuilder.start();
    } catch (IOException e) {
      throw new FCSHException("Error while trying to start the fcsh process", e);
    }

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

  public void clearTargets() {
    this.compilerTargets.clear();
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

  public CompilerTarget registerCompileTarget(List<String> arguments, int id) {
    synchronized (compilerTargets) {
      CompilerTarget compilerTarget = compilerTargets.get(arguments);
      if (compilerTarget == null) {
        compilerTarget = new CompilerTarget(id);
        compilerTargets.put(arguments, compilerTarget);
      }

      return compilerTarget;
    }
  }

  public CompilationResult mxmlc(List<String> arguments) throws FCSHException {
    assureFCSHIsActive();

    synchronized (compilerTargets) {
      CompilerTarget compilerTarget = compilerTargets.get(arguments);
      if (compilerTarget != null) {
        return compile(compilerTarget);
      }
    }

    MXMLCCommand mxmlcCommand = new MXMLCCommand(this, arguments);
    LOG.info("Compiling: " + mxmlcCommand.getCommand());

    submitCommand(mxmlcCommand);

    return mxmlcCommand.getCompileResult();
  }

  public CompilationResult compile(CompilerTarget target) throws FCSHException {
    assureFCSHIsActive();

    CompileTargetCommand compileCommand = new CompileTargetCommand(this, target);
    LOG.info("Compiling the target #" + target.getId());

    submitCommand(compileCommand);

    return compileCommand.getCompileResult();
  }

  public CompilationResult compc(List<String> arguments) throws FCSHException {
    assureFCSHIsActive();

    synchronized (compilerTargets) {
      CompilerTarget compilerTarget = compilerTargets.get(arguments);
      if (compilerTarget != null) {
        return compile(compilerTarget);
      }
    }

    COMPCCommand compcCommand = new COMPCCommand(this, arguments);
    LOG.info("Compiling: " + compcCommand.getCommand());

    submitCommand(compcCommand);

    return compcCommand.getCompileResult();
  }

  public void clear() throws FCSHException {
    if (livecodingMode) {
      // FCSH in livecoding mode clears itself after every compilation
//      clearTargets();
      return;
    }

    assureFCSHIsActive();

    ClearCommand clearCommand = new ClearCommand();
    submitCommand(clearCommand);

    clearTargets();
  }

  public FCSHProcessHandler getProcessHandler() {
    return fcshProcessHandler;
  }

  public void setLivecodingMode(boolean livecodingMode) {
    this.livecodingMode = livecodingMode;
  }
}
