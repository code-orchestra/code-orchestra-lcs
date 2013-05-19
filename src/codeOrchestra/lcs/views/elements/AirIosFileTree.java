package codeOrchestra.lcs.views.elements;

import java.io.File;
import java.util.List;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;

import scala.actors.threadpool.Arrays;
import codeOrchestra.lcs.errorhandling.ErrorHandler;
import codeOrchestra.lcs.project.LCSProject;
import codeOrchestra.lcs.views.elements.fileTree.FileTreeContentProvider;
import codeOrchestra.lcs.views.elements.fileTree.FileTreeLabelProvider;

/**
 * @author Oleg Chiruhin
 * @author Alexander Eliseyev
 */
public class AirIosFileTree {
  
  public static final String SAVED_PACKAGED_FILES_PREFERENCE_NAME = "__SAVED_PACKAGED_FILES";
  
  private Composite parentComponent;
  private IPreferenceStore preferenceStore;
  private Group optionsGroup;
  private CheckboxTreeViewer checkboxTreeViewer;
  private FileTreeContentProvider fileTreeContentProvider;
  private FileTreeLabelProvider fileTreeLabelProvider;
  private FileAlterationObserver faob;
  private FileAlterationMonitor monitor;

  private LCSProject currProject;
  
  public AirIosFileTree(Composite parentComponent, IPreferenceStore preferenceStore) {
    this.currProject = LCSProject.getCurrentProject();
    this.parentComponent = parentComponent;
    this.preferenceStore = preferenceStore;

    optionsGroup = new Group(parentComponent, SWT.SHADOW_ETCHED_IN);
    optionsGroup.setText("Package Contents:");
    GridLayout optionsGroupLayout = new GridLayout(1, false);
    optionsGroupLayout.marginHeight = 5;
    optionsGroupLayout.marginWidth = 5;
    optionsGroupLayout.marginBottom = 10;
    optionsGroup.setLayout(optionsGroupLayout);
    optionsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    createContents(optionsGroup);
  }

  public void saveOptions() {
    String savedFiles = "";
    Object[] fileObjects = getCheckboxTreeViewer().getCheckedElements();
    for (Object fileObject : fileObjects) {
      if (File.class.isAssignableFrom(fileObject.getClass())) {
        File currFile = (File) fileObject;
        savedFiles += currFile.getAbsolutePath() + ":";
      }
    }
    getPreferenceStore().setValue(SAVED_PACKAGED_FILES_PREFERENCE_NAME, savedFiles);
  }

  public void loadOptions() {
    String loadedFilesPref = getPreferenceStore().getString(SAVED_PACKAGED_FILES_PREFERENCE_NAME);
    @SuppressWarnings("unchecked")
    List<String> loadedFilePaths = Arrays.asList(loadedFilesPref.split(":"));

    List<File> fsFileObjects = fileTreeContentProvider.getElementsRecursive();
    for (Object fsFileObject : fsFileObjects) {
      if (File.class.isAssignableFrom(fsFileObject.getClass())) {
        File currFsFile = (File) fsFileObject;

        for (String loadedFilePath : loadedFilePaths) {
          File currFile = new File(loadedFilePath);
          if (currFsFile.getAbsolutePath().equals(currFile.getAbsolutePath())) {
            checkboxTreeViewer.setChecked(currFsFile, true);
          }
        }
      }

    }
  }

  private void lockOutputFile() {
    File outputFile = currProject.getOutputFile();
    List<File> fsFileObjects = fileTreeContentProvider.getElementsRecursive();
    for (Object fsFileObject : fsFileObjects) {
      if (File.class.isAssignableFrom(fsFileObject.getClass())) {
        File currFsFile = (File) fsFileObject;
        if (currFsFile.getAbsolutePath().equals(outputFile.getAbsolutePath())) {
          checkboxTreeViewer.setChecked(currFsFile, true);
        }
      }
    }

  }

  protected void createContents(Composite parent) {
    // Create the tree viewer to display the file tree
    checkboxTreeViewer = new CheckboxTreeViewer(parent);
    checkboxTreeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    
    File rootDir = currProject.getOutputDir();

    fileTreeContentProvider = new FileTreeContentProvider(rootDir);
    checkboxTreeViewer.setContentProvider(fileTreeContentProvider);
    fileTreeLabelProvider = new FileTreeLabelProvider();
    fileTreeLabelProvider.setPreserveCase(true);
    checkboxTreeViewer.setLabelProvider(fileTreeLabelProvider);
    checkboxTreeViewer.setInput("root"); // pass a non-null that will be ignored

    if (null != rootDir && null != checkboxTreeViewer) {
      // https://code.google.com/p/mdj-commons/source/browse/trunk/mdj-tools/mdj-fs-monitor/src/main/java/org/mdj/tools/fsmonitor/FSMonitor.java?spec=svn260&r=260
      faob = new FileAlterationObserver(rootDir);
      faob.addListener(new FileAlterationListener() {

        public void reloadTree() {
          Display.getDefault().asyncExec(new Runnable() {
            public void run() {
              checkboxTreeViewer.refresh();
              lockOutputFile();
            }
          });
        }

        @Override
        public void onStop(FileAlterationObserver arg0) {
          reloadTree();
        }

        @Override
        public void onStart(FileAlterationObserver arg0) {
          reloadTree();
        }

        @Override
        public void onFileDelete(File arg0) {
          reloadTree();
        }

        @Override
        public void onFileCreate(File arg0) {
          reloadTree();
        }

        @Override
        public void onFileChange(File arg0) {
          reloadTree();
        }

        @Override
        public void onDirectoryDelete(File arg0) {
          reloadTree();
        }

        @Override
        public void onDirectoryCreate(File arg0) {
          reloadTree();
        }

        @Override
        public void onDirectoryChange(File arg0) {
          reloadTree();
        }
      });
      long interval = 1000;
      monitor = new FileAlterationMonitor(interval);
      monitor.addObserver(faob);

      try {
        monitor.start();
      } catch (Exception e) {
        ErrorHandler.handle(e, "Error while starting file changes monitor");
      }

    }

    // When user checks a checkbox in the tree, check all its children
    checkboxTreeViewer.addCheckStateListener(new ICheckStateListener() {
      public void checkStateChanged(CheckStateChangedEvent event) {
        // If the item is checked . . .
        Object element = event.getElement();
        if (event.getChecked()) {
          // . . . check all its children
          checkboxTreeViewer.setSubtreeChecked(element, true);
        }
        File outputFile = currProject.getOutputFile();
        if (((File) element).getAbsolutePath().equals(outputFile.getAbsolutePath())) {
          checkboxTreeViewer.setChecked(element, true);
        }
      }
    });
  }

  public void removeFileMonitor() {
    try {
      if (null != monitor) {
        monitor.stop();
        monitor = null;
      }
    } catch (Exception e) {
      ErrorHandler.handle(e, "Error while monitoring file changes");
    }
  }

  public Composite getParentComponent() {
    return parentComponent;
  }

  public void setParentComponent(Composite parentComponent) {
    this.parentComponent = parentComponent;
  }

  public IPreferenceStore getPreferenceStore() {
    return preferenceStore;
  }

  public void setPreferenceStore(IPreferenceStore preferenceStore) {
    this.preferenceStore = preferenceStore;
  }

  public Group getOptionsGroup() {
    return optionsGroup;
  }

  public void setOptionsGroup(Group optionsGroup) {
    this.optionsGroup = optionsGroup;
  }

  public CheckboxTreeViewer getCheckboxTreeViewer() {
    return checkboxTreeViewer;
  }

  public void setCheckboxTreeViewer(CheckboxTreeViewer checkboxTreeViewer) {
    this.checkboxTreeViewer = checkboxTreeViewer;
  }

  public FileTreeContentProvider getFileTreeContentProvider() {
    return fileTreeContentProvider;
  }

  public void setFileTreeContentProvider(FileTreeContentProvider fileTreeContentProvider) {
    this.fileTreeContentProvider = fileTreeContentProvider;
  }

  public FileTreeLabelProvider getFileTreeLabelProvider() {
    return fileTreeLabelProvider;
  }

  public void setFileTreeLabelProvider(FileTreeLabelProvider fileTreeLabelProvider) {
    this.fileTreeLabelProvider = fileTreeLabelProvider;
  }

}
