/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.ui.css.swt.properties.custom;

import java.lang.reflect.Method;
import org.eclipse.e4.ui.css.core.dom.properties.ICSSPropertyHandler;
import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.e4.ui.css.swt.properties.AbstractCSSPropertySWTHandler;
import org.eclipse.e4.ui.widgets.CTabFolder;
import org.eclipse.e4.ui.widgets.CTabFolderRenderer;
import org.eclipse.swt.widgets.Control;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;

public class CSSPropertyCornerRadiusSWTHandler extends AbstractCSSPropertySWTHandler {

	
	public static final ICSSPropertyHandler INSTANCE = new CSSPropertyCornerRadiusSWTHandler();
	
	protected void applyCSSProperty(Control control, String property,
			CSSValue value, String pseudo, CSSEngine engine) throws Exception {
		if (!(control instanceof CTabFolder)) return;
		if (value.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
			int radiusValue = (int) ((CSSPrimitiveValue) value).getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
			CTabFolderRenderer renderer = ((CTabFolder) control).getRenderer();
			Method m = renderer.getClass().getMethod("setCornerRadius", new Class[]{int.class});
			m.invoke(renderer, radiusValue);
		}
	}
	
	protected String retrieveCSSProperty(Control control, String property,
			String pseudo, CSSEngine engine) throws Exception {
		return null;
	}

}
