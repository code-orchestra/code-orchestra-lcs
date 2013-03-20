package codeOrchestra.lcs.sources;

import java.io.File;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public class SourcesTracker {
	
	private List<File> sourceDirs;

	private SourcesState lastState;
	
	public SourcesTracker(List<File> sourceDirs) {
		this.sourceDirs = sourceDirs;
		this.lastState = capture();
	}

	private SourcesState capture() {
		return SourcesState.capture(sourceDirs);
	}
	
	public List<File> getChangedFiles() {
		SourcesState newState = capture();
		return newState.getChangedFiles(lastState);
	}
	
}
