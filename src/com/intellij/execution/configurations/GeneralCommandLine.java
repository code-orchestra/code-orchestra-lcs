/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.execution.configurations;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessNotCreatedException;
import com.intellij.openapi.util.SystemInfo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class GeneralCommandLine {
  private Map<String, String> myEnvParams;
  private boolean myPassParentEnvs;
  private String myExePath = null;
  private File myWorkDirectory = null;
  private ParametersList myProgramParams = new ParametersList();
  private Charset myCharset = getDefaultSystemCharset();

  public static Charset getDefaultSystemCharset() {
    Charset charset = null;
    try {
      charset = Charset.forName(System.getProperty("file.encoding"));
    } catch (Exception e) {
      // Null is OK here.
    }

    return charset;
  }
  
  public void setExePath(final String exePath) {
    myExePath = exePath.trim();
  }

  public String getExePath() {
    return myExePath;
  }

  public void setWorkDirectory(final String path) {
    setWorkingDirectory(path != null? new File(path) : null);
  }

  public void setWorkingDirectory(final File workingDirectory) {
    myWorkDirectory = workingDirectory;
  }

  public void setEnvParams(final Map<String, String> envParams) {
    myEnvParams = envParams;
  }

  public void setCharset(Charset charset) {
    myCharset = charset;
  }

  public void setCharsetAndAddJavaParameter(Charset charset) {
    myCharset = charset;
    addParameter("-Dfile.encoding=" + charset.name());
  }

  public void addParameters(final String... parameters) {
    for (String parameter : parameters) {
      addParameter(parameter);
    }
  }

  public void addParameters(final List<String> parameters) {
    for (final String parameter : parameters) {
      addParameter(parameter);
    }
  }

  public void addParameter(final String parameter) {
    myProgramParams.add(parameter);
  }

  public String getCommandLineString() {
    final StringBuffer buffer = new StringBuffer(quoteParameter(myExePath));
    appendParams( buffer );
    return buffer.toString();
  }

  public String getCommandLineParams() {
    final StringBuffer buffer = new StringBuffer();
    appendParams( buffer );
    return buffer.toString();
  }

  private void appendParams( StringBuffer buffer ) {
    for( final String param : myProgramParams.getList() ) {
      buffer.append(" ").append(quoteParameter(param));
    }
  }

  public Charset getCharset() {
    return myCharset;
  }

  public Process createProcess() throws ExecutionException {
    checkWorkingDirectory();
    try {
      final String[] commands = getCommands();
      if(commands[0] == null) throw new ExecutionException("run.configuration.error.executable.not.specified");

      return myWorkDirectory != null
             ? Runtime.getRuntime().exec(commands, getEnvParamsArray(), myWorkDirectory)
             : Runtime.getRuntime().exec(commands, getEnvParamsArray());
    }
    catch (IOException e) {
      throw new ProcessNotCreatedException(e.getMessage(), e, this);
    }
  }

  private void checkWorkingDirectory() throws ExecutionException {
    if (myWorkDirectory == null) {
      return;
    }
    if (!myWorkDirectory.exists()) {
      throw new ExecutionException("run.configuration.error.working.directory.does.not.exist " + myWorkDirectory.getAbsolutePath());
    }
    if (!myWorkDirectory.isDirectory()) {
      throw new ExecutionException("run.configuration.error.working.directory.not.directory");
    }
  }

  public Map<String, String> getEnvParams() {
    return myEnvParams;
  }

  private String[] getEnvParamsArray() {
    final Map<String, String> envParams = collectEnvParams();
    if (envParams == null) return null;
    final String[] result = new String[envParams.size()];
    int i=0;
    for (final String key : envParams.keySet()) {
      result[i++] = key + "=" + envParams.get(key).trim();
    }
    return result;
  }

  private Map<String, String> collectEnvParams() {
    if (myEnvParams == null) {
      return null;
    }
    final Map<String, String> envParams = new HashMap<String, String>();
    if (myPassParentEnvs) {
      envParams.putAll(System.getenv());
    }
    envParams.putAll(myEnvParams);
    return envParams;
  }

  public String[] getCommands() {
    final List<String> parameters = myProgramParams.getList();
    final String[] result = new String[parameters.size() + 1];
    result[0] = myExePath;
    int index = 1;
    for (Iterator<String> iterator = parameters.iterator(); iterator.hasNext(); index++) {
      result[index] = iterator.next();
    }
    return result;
  }

  public ParametersList getParametersList() {
    return myProgramParams;
  }

  public static String quoteParameter(final String param) {
    if (!SystemInfo.isWindows) {
      return param;
    }
    return quote(param);
  }

  public static String quote(final String parameter) {
    if (parameter == null || !hasWhitespace(parameter)) {
      return parameter; // no need to quote
    }
    if (parameter.length() >= 2 && parameter.startsWith("\"") && parameter.endsWith("\"")) {
      return parameter; // already quoted
    }
    // need to escape trailing slash if any, otherwise it will escape the ending quote
    return "\"" + parameter + (parameter.endsWith("\\")? "\\\"" : "\"");
  }

  private static boolean hasWhitespace(final String string) {
    final int length = string.length();
    for (int i = 0; i < length; i++) {
      if (Character.isWhitespace(string.charAt(i))) {
        return true;
      }
    }
    return false;
  }

  public GeneralCommandLine clone() {
    final GeneralCommandLine clone = new GeneralCommandLine();
    clone.myCharset = myCharset;
    clone.myExePath = myExePath;
    clone.myWorkDirectory = myWorkDirectory;
    clone.myProgramParams = myProgramParams.clone();
    clone.myEnvParams = myEnvParams != null ? new HashMap<String, String>(myEnvParams) : null;
    return clone;
  }

  public void setPassParentEnvs(final boolean passParentEnvs) {
    myPassParentEnvs = passParentEnvs;
  }

}
