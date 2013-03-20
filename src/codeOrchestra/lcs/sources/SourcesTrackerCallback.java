package codeOrchestra.lcs.sources;

import java.io.File;

/**
 * @author Alexander Eliseyev
 */
public interface SourcesTrackerCallback {

	void sourceFileChanged(File sourceFile);
	
}
