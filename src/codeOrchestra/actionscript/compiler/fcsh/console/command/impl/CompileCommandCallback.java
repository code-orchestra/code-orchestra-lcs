package codeOrchestra.actionscript.compiler.fcsh.console.command.impl;

import codeOrchestra.actionscript.compiler.fcsh.FCSHException;
import codeOrchestra.actionscript.compiler.fcsh.console.command.CommandCallback;
import codeOrchestra.actionscript.modulemaker.CompilationResult;

/**
 * @author Alexander Eliseyev
 */
public interface CompileCommandCallback extends CommandCallback {

  CompilationResult getCompileResult() throws FCSHException;

}
