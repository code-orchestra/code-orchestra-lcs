package codeOrchestra.lcs;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;

public class DarkThemeHandler {
	@Execute
	   public void execute(IThemeEngine engine) {
	      engine.setTheme("com.codeorchestra.lcs.themes.dark",false);
	      //org.eclipse.e4.ui.css.theme.e4_default
	   }
}
