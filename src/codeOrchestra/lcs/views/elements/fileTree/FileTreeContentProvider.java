package codeOrchestra.lcs.views.elements.fileTree;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import codeOrchestra.lcs.project.CompilerSettings;
import codeOrchestra.lcs.project.LCSProject;

public class FileTreeContentProvider implements ITreeContentProvider {

	private File root;

	public FileTreeContentProvider(File root) {
		this.root = root;
	}
	/**
	 * Gets the children of the specified object
	 * 
	 * @param arg0
	 *            the parent object
	 * @return Object[]
	 */
	public Object[] getChildren(Object arg0) {
		// Return the files and subdirectories in this directory
		return ((File) arg0).listFiles();
	}

	/**
	 * Gets the parent of the specified object
	 * 
	 * @param arg0
	 *            the object
	 * @return Object
	 */
	public Object getParent(Object arg0) {
		// Return this file's parent file
		return ((File) arg0).getParentFile();
	}

	/**
	 * Returns whether the passed object has children
	 * 
	 * @param arg0
	 *            the parent object
	 * @return boolean
	 */
	public boolean hasChildren(Object arg0) {
		// Get the children
		Object[] obj = getChildren(arg0);

		// Return whether the parent has children
		return obj == null ? false : obj.length > 0;
	}

	/**
	 * Gets the root element(s) of the tree
	 * 
	 * @param arg0
	 *            the input data
	 * @return Object[]
	 */
	public Object[] getElements(Object arg0) {
		if (null==root) {
			// These are the root elements of the tree
			// We don't care what arg0 is, because we just want all
			// the root nodes in the file system
			return File.listRoots();
		} else {
			File outputFile = LCSProject.getCurrentProject().getOutputFile();
			
			Object[] fileObjects = root.listFiles();
			
			if (!outputFile.exists()) {
				Object[] newFileObjects = appendElementToArray(fileObjects, outputFile);
				fileObjects = newFileObjects;
			}
			
			return fileObjects;
		}
	}
	
	//http://stackoverflow.com/questions/2843366/how-to-add-new-elements-to-an-array
	static <T> T[] appendElementToArray(T[] arr, T element) {
	    final int N = arr.length;
	    arr = Arrays.copyOf(arr, N + 1);
	    arr[N] = element;
	    return arr;
	}
	
	public List<File> getElementsRecursive() {
		List<File> result = new ArrayList<File>();
		Object[] roots = getElements(null);
		for (int i=0; i<roots.length; i++) {
			List<File> subFiles = getChildFilesRecursive((File)roots[i]);
			if (null!=subFiles) {
				result.addAll(subFiles);
			}
		}
		return result;
	}
	
	public List<File> getChildFilesRecursive(File rootFile) {
		List<File> result = new ArrayList<File>();
		if (null!=rootFile) {
			result.add(rootFile);
			File[] children = (File[]) getChildren(rootFile);
			if (null!=children) {
				for (int i=0;i<children.length;i++) {
					List<File> subFiles = getChildFilesRecursive((File)children[i]);
					if (null!=subFiles) {
						result.addAll(subFiles);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Disposes any created resources
	 */
	public void dispose() {
		// Nothing to dispose
	}

	/**
	 * Called when the input changes
	 * 
	 * @param arg0
	 *            the viewer
	 * @param arg1
	 *            the old input
	 * @param arg2
	 *            the new input
	 */
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		// Nothing to change
	}
}
