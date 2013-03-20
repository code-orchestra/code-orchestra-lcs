package codeOrchestra.lcs.sources;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Eliseyev
 */
public class SourcesState {

	public static SourcesState capture(List<File> dirs) {
		SourcesState sourcesState = new SourcesState();
		
		// TODO: implement
		
		return sourcesState;
	}
	
	private Map<String, Long> state = new HashMap<String, Long>();
	
	private SourcesState() {		
	}

	public List<File> getChangedFiles(SourcesState oldState) {
		// TODO: implement
		return null;
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
