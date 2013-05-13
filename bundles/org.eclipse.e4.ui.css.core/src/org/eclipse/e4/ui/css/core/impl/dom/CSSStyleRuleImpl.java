/*******************************************************************************
 * Copyright (c) 2008 Angelo Zerr and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *     IBM Corporation
 *******************************************************************************/

package org.eclipse.e4.ui.css.core.impl.dom;

import java.io.Serializable;

import org.eclipse.e4.ui.css.core.dom.CSSPropertyList;
import org.eclipse.e4.ui.css.core.dom.ExtendedCSSRule;
import org.w3c.css.sac.SelectorList;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

public class CSSStyleRuleImpl extends CSSRuleImpl implements CSSStyleRule, ExtendedCSSRule, Serializable {

	private SelectorList selectors;
	private CSSStyleDeclarationImpl styleDeclaration;
	
	public CSSStyleRuleImpl(CSSStyleSheet parentStyleSheet, CSSRule parentRule,
			SelectorList selectors) {
		super(parentStyleSheet, parentRule);
		this.selectors = selectors;
	}

	//----------------------------------------
	// W3C CSSRule API methods

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.css.CSSRule.getType()
	 */
	public short getType() {
		return CSSRule.STYLE_RULE;
	}
	
	
	//----------------------------------------
	// W3C CSSStyleRule API methods
	
	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.css.CSSStyleRule#getSelectorText()
	 */
	public String getSelectorText() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.css.CSSStyleRule#getStyle()
	 */
	public CSSStyleDeclaration getStyle() {
		return styleDeclaration;
	}

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.css.CSSStyleRule#setSelectorText(String)
	 */
	public void setSelectorText(String selectorText) throws DOMException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	
	//----------------------------------------
	// Additional methods

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.e4.ui.css.core.dom#ExtendedCSSRule#getSelectorList()
	 */
	public SelectorList getSelectorList() {
		return selectors;
	}


	public CSSPropertyList getCSSPropertyList() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	public void setStyle(CSSStyleDeclarationImpl styleDeclaration) {
		this.styleDeclaration = styleDeclaration;
	}
}
