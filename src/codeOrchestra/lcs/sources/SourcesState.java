package codeOrchestra.lcs.sources;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codeOrchestra.utils.FileUtils;

/**
 * @author Alexander Eliseyev
 */
public class SourcesState {

	public static SourcesState capture(List<File> dirs) {
		SourcesState sourcesState = new SourcesState();
		
		for (File baseDir : dirs) {
			for (File sourceFile : FileUtils.listFileRecursively(baseDir, FileUtils.FILES_ONLY_FILTER)) {
				sourcesState.addFile(sourceFile, baseDir);
			}
		}
		
		return sourcesState;
	}
	
	private Map<String, Long> state = new HashMap<String, Long>();
	private Map<String, SourceFile> pathToWrapper = new HashMap<String, SourceFile>();
	
	private SourcesState() {		
	}

	public void addFile(File file, File baseDir) {
		state.put(file.getPath(), file.lastModified());
		pathToWrapper.put(file.getPath(), new SourceFile(file, baseDir.getPath()));
	}
	
	public List<SourceFile> getChangedFiles(SourcesState oldState) {
		List<SourceFile> changedFiles = new ArrayList<SourceFile>();
		
		for (String newStatePath : state.keySet()) {
			long newTimestamp = state.get(newStatePath);
			
			if (oldState.state.containsKey(newStatePath)) {
				long oldTimestamp = oldState.state.get(newStatePath);
				if (newTimestamp != oldTimestamp) {
					changedFiles.add(pathToWrapper.get(newStatePath));
				}
			} else {
			  changedFiles.add(pathToWrapper.get(newStatePath));
			}
		}
		
		return changedFiles;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourcesState other = (SourcesState) obj;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}	
	
}
