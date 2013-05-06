package codeOrchestra.lcs.air;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.IWorkbenchWindow;

import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.views.AirIosSettingsView;
import codeOrchestra.lcs.views.CompilerSettingsView;
import codeOrchestra.lcs.views.elements.AirIosOption;
import codeOrchestra.lcs.views.elements.AirIosOptions;
import codeOrchestra.lcs.views.elements.ViewHelper;
import codeOrchestra.utils.PathUtils;
import codeOrchestra.utils.TemplateCopyUtil;

public class AirIosIpaBuildScriptGenerator {

	private LCSProject project;
	private String appName;
	IWorkbenchWindow window;

	public AirIosIpaBuildScriptGenerator(LCSProject project, String appName) {
		this.project = project;
		this.appName = appName;
	}

	public AirIosIpaBuildScriptGenerator(LCSProject project, String appName, IWorkbenchWindow window) {
		this.project = project;
		this.appName = appName;
		this.window = window;
	}
	
	public static boolean checkChild(File maybeChild, File possibleParent) throws IOException
	{
	    final File parent = possibleParent.getCanonicalFile();
	    if (!parent.exists() || !parent.isDirectory()) {
	        // this cannot possibly be the parent
	        return false;
	    }

	    File child = maybeChild.getCanonicalFile();
	    while (child != null) {
	        if (child.equals(parent)) {
	            return true;
	        }
	        child = child.getParentFile();
	    }
	    // No match found, and we've hit the root directory
	    return false;
	}

	public String generate() throws IOException {
		File targetScriptFile = getSctiptPath(project);
		File templateFile = new File(PathUtils.getTemplaesDir(), "airIosIpaBuild.sh");
		
		File targetDescScriptFile = getDescScriptPath(project);
		File templateDescFile = new File(PathUtils.getTemplaesDir(), "AppName-app.xml");

		Map<String, String> replacements = new HashMap<String, String>();
		replacements.put("{FLEX_SDK}", project.getCompilerSettings().getFlexSDKPath());
		replacements.put("{APPNAME}", appName);
		replacements.put("{PROJECT_DIR}", project.getBaseDir().getAbsolutePath());
		replacements.put("{OUTPUT_DIR}", project.getOutputDir().getAbsolutePath());

		AirIosSettingsView aisv = (AirIosSettingsView) ViewHelper.getView(window, AirIosSettingsView.ID);
		if (null!=aisv) {
			LCSProject currProject = LCSProject.getCurrentProject();
			File outputDir = currProject.getOutputDir();
			File baseDir = currProject.getBaseDir();
			replacements.put("{IPA_FILE}", appName+".ipa");
			replacements.put("{DESCRIPTOR_FILE}", appName+"-app.xml");
			
			
			AirIosOptions aioParent = aisv.getAirIosOptions();
			List<AirIosOption> aio = aioParent.getOptions();
			for (AirIosOption currOption : aio) {
				replacements.put("{"+currOption.getName()+"}", currOption.getValue());
			}
			
			String packagedFiles = "";
			
			Object[] fileObjects = aisv.getAirIosFileTree().getCheckboxTreeViewer().getCheckedElements();
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
				packagedFiles+="\""+relative+"\" ";
			}
		
			
			replacements.put("{PACKAGED_FILES}", packagedFiles);
		}

		TemplateCopyUtil.copy(templateFile, targetScriptFile, replacements);
		targetScriptFile.setExecutable(true);
		
		Map<String, String> descReplacements = new HashMap<String, String>();
		descReplacements.put("{APPNAME}", appName);
		
		CompilerSettingsView csv = (CompilerSettingsView) ViewHelper.getView(window, CompilerSettingsView.ID);
		if (null!=csv) {
			String ouputFileName = csv.getOutputFileName();
			descReplacements.put("{OUTPUT_FILE}", ouputFileName);
		}
		
		TemplateCopyUtil.copy(templateDescFile, targetDescScriptFile, descReplacements);

		return targetScriptFile.getPath();
	}

	public static File getSctiptPath(LCSProject project) {
		return new File(project.getOutputDir(), "airIosIpaBuild.sh");
	}
	
	public File getDescScriptPath(LCSProject project) {
		return new File(project.getOutputDir(), appName+"-app.xml");
	}

}
