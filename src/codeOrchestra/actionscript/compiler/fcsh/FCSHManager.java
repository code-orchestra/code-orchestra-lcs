package codeOrchestra.actionScript.compiler.fcsh;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codeOrchestra.actionScript.compiler.fcsh.console.command.CommandCallback;
import codeOrchestra.actionScript.compiler.fcsh.console.command.FCSHCommandExecuteThread;
import codeOrchestra.actionScript.compiler.fcsh.console.command.FCSHCommandRunnable;
import codeOrchestra.actionScript.compiler.fcsh.console.command.impl.ClearCommand;
import codeOrchestra.actionScript.compiler.fcsh.console.command.impl.CompileTargetCommand;
import codeOrchestra.actionScript.compiler.fcsh.console.command.impl.LivecodingBaseCOMPCCommand;
import codeOrchestra.actionScript.compiler.fcsh.console.command.impl.LivecodingBaseMXMLCCommand;
import codeOrchestra.actionScript.compiler.fcsh.console.command.impl.LivecodingCachesDeleteCommand;
import codeOrchestra.actionScript.compiler.fcsh.console.command.impl.LivecodingIncrementalCOMPCCommand;
import codeOrchestra.actionScript.compiler.fcsh.target.CompilerCommand;
import codeOrchestra.actionScript.compiler.fcsh.target.CompilerTarget;
import codeOrchestra.actionScript.modulemaker.CompilationResult;
import codeOrchestra.lcs.fcsh.FCSHProcessHandler;
import codeOrchestra.lcs.logging.Logger;

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
  private Map<CompilerCommand, CompilerTarget> compilerTargets = Collections.synchronizedMap(new HashMap<CompilerCommand, CompilerTarget>());

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

    clearTargets();

    FCSHLauncher fcshLauncher = new FCSHLauncher();
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

  public CompilerTarget registerCompileTarget(String executableName, List<String> arguments, int id) {
    synchronized (compilerTargets) {
      CompilerCommand compilerCommand = new CompilerCommand(arguments, executableName);
      CompilerTarget compilerTarget = compilerTargets.get(compilerCommand);
      if (compilerTarget == null) {
        compilerTarget = new CompilerTarget(id);
        compilerTargets.put(compilerCommand, compilerTarget);
      }

      return compilerTarget;
    }
  }

  public CompilationResult baseMXMLC(List<String> arguments) throws FCSHException {
    assureFCSHIsActive();

    synchronized (compilerTargets) {
      CompilerCommand compilerCommand = new CompilerCommand(arguments, LivecodingBaseMXMLCCommand.EXECUTABLE_NAME);
      CompilerTarget compilerTarget = compilerTargets.get(compilerCommand);
      if (compilerTarget != null) {
        return compile(compilerTarget);
      }
    }

    LivecodingBaseMXMLCCommand mxmlcCommand = new LivecodingBaseMXMLCCommand(this, arguments);
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

  public CompilationResult baseCOMPC(List<String> arguments) throws FCSHException {
    assureFCSHIsActive();

    synchronized (compilerTargets) {
      CompilerCommand compilerCommand = new CompilerCommand(arguments, LivecodingBaseCOMPCCommand.EXECUTABLE_NAME);
      CompilerTarget compilerTarget = compilerTargets.get(compilerCommand);
      if (compilerTarget != null) {
        return compile(compilerTarget);
      }
    }

    LivecodingBaseCOMPCCommand compcCommand = new LivecodingBaseCOMPCCommand(this, arguments);
    LOG.info("Compiling: " + compcCommand.getCommand());

    submitCommand(compcCommand);

    return compcCommand.getCompileResult();
  }
  
  public CompilationResult incrementalCOMPC(List<String> arguments) throws FCSHException {
    assureFCSHIsActive();

    /*
    synchronized (compilerTargets) {
      CompilerCommand compilerCommand = new CompilerCommand(arguments, LivecodingIncrementalCOMPCCommand.EXECUTABLE_NAME);
      CompilerTarget compilerTarget = compilerTargets.get(compilerCommand);
      if (compilerTarget != null) {
        return compile(compilerTarget);
      }
    }
    */

    LivecodingIncrementalCOMPCCommand compcCommand = new LivecodingIncrementalCOMPCCommand(this, arguments);
    LOG.info("Compiling: " + compcCommand.getCommand());

    submitCommand(compcCommand);

    return compcCommand.getCompileResult();
  }

  public void deleteLivecodingCaches() throws FCSHException {
    assureFCSHIsActive();

    LivecodingCachesDeleteCommand deleteCachesCommand = new LivecodingCachesDeleteCommand();
    submitCommand(deleteCachesCommand);
  }
  
  public void clear() throws FCSHException {
    // FCSH in livecoding mode clears itself after every compilation
    if (true) {
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

}
