package codeOrchestra.lcs.air.views;

/**
 * @author Alexander Eliseyev
 */
public enum AirOptionType {
	
	STRING,
	PASSWORD,
	FILE,
	DIRECTORY;
	
	public boolean isFileType() {
		return this == FILE || this == DIRECTORY;
	}
	
}
