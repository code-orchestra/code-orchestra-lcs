package codeOrchestra.lcs.air.views;

/**
 * @author Alexander Eliseyev
 */
public class AirOptionSource {
	
	private String title;
	private String name;
	private String compilerSetting;
	private AirOptionType optionType;
	
	public AirOptionSource(String title, String name, String compilerSetting, AirOptionType optionType) {
		this.title = title;
		this.name = name;
		this.compilerSetting = compilerSetting;
		this.optionType = optionType;
	}

	public AirOptionSource(String name, AirOptionType optionType) {
		this("-" + name, name, "-" + name, optionType);
	}

	public String getTitle() {
		return title;
	}

	public String getName() {
		return name;
	}

	public String getCompilerSetting() {
		return compilerSetting;
	}

	public AirOptionType getOptionType() {
		return optionType;
	}
	
	
}
