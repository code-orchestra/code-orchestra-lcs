package codeOrchestra.lcs.air.android;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.IWorkbenchWindow;

import com.intellij.openapi.util.SystemInfo;

import codeOrchestra.lcs.air.AirBuildScriptGenerator;
import codeOrchestra.lcs.air.views.AirFileTree;
import codeOrchestra.lcs.air.views.AirOption;
import codeOrchestra.lcs.air.views.AirOptions;
import codeOrchestra.lcs.air.views.utils.ViewHelper;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.views.CompilerSettingsView;
import codeOrchestra.utils.PathUtils;
import codeOrchestra.utils.TemplateCopyUtil;

/**
 * @author Alexander Eliseyev
 */
public class AirAndroidApkBuildScriptGenerator extends AirBuildScriptGenerator {

	public AirAndroidApkBuildScriptGenerator(LCSProject project, IWorkbenchWindow window) {
    super(project, window);
  }

  public AirAndroidApkBuildScriptGenerator(LCSProject project) {
    super(project);
  }

  @Override
	public String generate(AirOptions aioParent, AirFileTree fileTree) throws IOException {
    File targetScriptFile = getScriptPath(project);
    File templateFile = new File(PathUtils.getTemplatesDir(), getScriptFileName());

    File targetDescScriptFile = getDescScriptPath(project);
    File templateDescFile = new File(PathUtils.getTemplatesDir(), "AppName-app.xml");

    Map<String, String> replacements = new HashMap<String, String>();
    replacements.put("{FLEX_SDK}", project.getCompilerSettings().getFlexSDKPath());
    replacements.put("{APPNAME}", appName);
    replacements.put("{PROJECT_DIR}", project.getBaseDir().getAbsolutePath());
    String outputDirPath = project.getOutputDir().getAbsolutePath();
    if (SystemInfo.isWindows && !outputDirPath.endsWith(File.separator)) {
      outputDirPath += File.separator;
    }
    replacements.put("{OUTPUT_DIR}", outputDirPath);

    LCSProject currProject = LCSProject.getCurrentProject();
    File outputDir = currProject.getOutputDir();
    replacements.put("{APK_FILE}", appName + ".apk");
    replacements.put("{DESCRIPTOR_FILE}", appName + "-app.xml");

    List<AirOption> aio = aioParent.getOptions();
    for (AirOption currOption : aio) {
      replacements.put("{" + currOption.getName() + "}", currOption.getValue());
    }

    String packagedFiles = "";

    Object[] fileObjects = fileTree.getCheckboxTreeViewer().getCheckedElements();
    List<File> fileObjectList = new ArrayList<File>();
    List<File> dirObjectList = new ArrayList<File>();
    List<File> legalDirObjectList = new ArrayList<File>();
    List<File> allFilesObjectList = new ArrayList<File>();

    for (Object fileObject : fileObjects) {
      if (File.class.isAssignableFrom(fileObject.getClass())) {
        File currFile = (File) fileObject;
        if (!currFile.isDirectory()) {
          fileObjectList.add(currFile);
        } else {
          dirObjectList.add(currFile);
        }
      }
    }

    for (File currDir : dirObjectList) {
      boolean found = false;
      for (File currFile : fileObjectList) {
        if (checkChild(currFile, currDir)) {
          found = true;
          break;
        }
        if (!found) {
          legalDirObjectList.add(currDir);
        }
      }
    }

    allFilesObjectList.addAll(legalDirObjectList);
    allFilesObjectList.addAll(fileObjectList);

    for (File currFile : fileObjectList) {
      String relative = new File(outputDir.getAbsolutePath()).toURI().relativize(new File(currFile.getAbsolutePath()).toURI()).getPath();
      packagedFiles += "\"" + relative + "\" ";
    }

    replacements.put("{PACKAGED_FILES}", packagedFiles);

    TemplateCopyUtil.copy(templateFile, targetScriptFile, replacements);
    targetScriptFile.setExecutable(true);

    Map<String, String> descReplacements = new HashMap<String, String>();
    descReplacements.put("{APPNAME}", appName);

    CompilerSettingsView csv = (CompilerSettingsView) ViewHelper.getView(window, CompilerSettingsView.ID);
    if (null != csv) {
      String ouputFileName = csv.getOutputFileName();
      descReplacements.put("{OUTPUT_FILE}", ouputFileName);
    }

    TemplateCopyUtil.copy(templateDescFile, targetDescScriptFile, descReplacements);

    return targetScriptFile.getPath();
  }

  @Override
  protected String getScriptFileName() {
    if (SystemInfo.isWindows) {
      return "airAndroidApkBuild.bat";
    } else {
      return "airAndroidApkBuild.sh";
    }
  }
	
}
