/*******************************************************************************
 * Copyright (c) 2008 Angelo Zerr and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.ui.css.core.utils;

/**
 * Class utils.
 */
public class ClassUtils {

	/**
	 * Return the simple name of Class <code>c</code>.
	 * 
	 * @param c
	 * @return
	 */
	public static String getSimpleName(Class c) {
		String name = c.getName();
		int index = name.lastIndexOf(".");
		if (index > 0) {
			name = name.substring(index + 1, name.length());
		}
		return name;
	}

	/**
	 * Return the package name of Class <code>c</code>.
	 * 
	 * @param c
	 * @return
	 */
	public static String getPackageName(Class c) {
		String name = c.getName();
		int index = name.lastIndexOf(".");
		if (index > 0) {
			return name.substring(0, index);
		}
		return null;
	}
}
