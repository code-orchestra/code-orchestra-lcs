/*******************************************************************************
 * Copyright (c) 2008, 2009 Angelo Zerr and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *     IBM Corporation - ongoing development
 *******************************************************************************/
package org.eclipse.e4.ui.css.core.impl.dom;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.ui.css.core.dom.ExtendedCSSRule;
import org.eclipse.e4.ui.css.core.impl.sac.ExtendedSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;
import org.w3c.dom.Element;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.DocumentCSS;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.stylesheets.StyleSheetList;
import org.w3c.dom.views.DocumentView;


/**
 * {@link ViewCSS} implementation used to compute {@link CSSStyleDeclaration}.
 */
public class ViewCSSImpl implements ViewCSS {

	protected DocumentCSS documentCSS;

	/**
	 * Creates a new ViewCSS.
	 */
	public ViewCSSImpl(DocumentCSS documentCSS) {
		this.documentCSS = documentCSS;
	}

	/**
	 * <b>DOM</b>: Implements {@link
	 * org.w3c.dom.views.AbstractView#getDocument()}.
	 */
	public DocumentView getDocument() {
		return null;
	}

	/**
	 * <b>DOM</b>: Implements {@link
	 * org.w3c.dom.css.ViewCSS#getComputedStyle(Element,String)}.
	 */
	public CSSStyleDeclaration getComputedStyle(Element elt, String pseudoElt) {
		// Loop for CSS StyleSheet list parsed
		StyleSheetList styleSheetList = documentCSS.getStyleSheets();
		int l = styleSheetList.getLength();
		for (int i = 0; i < l; i++) {
			CSSStyleSheet styleSheet = (CSSStyleSheet) styleSheetList.item(i);
			CSSStyleDeclaration styleDeclaration = getComputedStyle(styleSheet,
					elt, pseudoElt);
			if (styleDeclaration != null)
				return styleDeclaration;
		}
		return null;
	}

	public CSSStyleDeclaration getComputedStyle(CSSStyleSheet styleSheet,
			Element elt, String pseudoElt) {
		List styleDeclarations = null;
		StyleWrapper firstStyleDeclaration = null;
		CSSRuleList ruleList = styleSheet.getCssRules();
		int length = ruleList.getLength();
		int position = 0;
		for (int i = 0; i < length; i++) {
			CSSRule rule = ruleList.item(i);
			switch (rule.getType()) {
			case CSSRule.STYLE_RULE: {
				CSSStyleRule styleRule = (CSSStyleRule) rule;
				if (rule instanceof ExtendedCSSRule) {
					ExtendedCSSRule r = (ExtendedCSSRule) rule;
					SelectorList selectorList = r.getSelectorList();
					// Loop for SelectorList
					int l = selectorList.getLength();
					for (int j = 0; j < l; j++) {
						Selector selector = (Selector) selectorList.item(j);
						if (selector instanceof ExtendedSelector) {
							ExtendedSelector extendedSelector = (ExtendedSelector) selector;
							if (extendedSelector.match(elt, pseudoElt)) {
								CSSStyleDeclaration style = styleRule
										.getStyle();
								int specificity = extendedSelector
										.getSpecificity();
								StyleWrapper wrapper = new StyleWrapper(style,
										specificity, position++);
								if (firstStyleDeclaration == null) {
									firstStyleDeclaration = wrapper;
								} else {
									// There is several Style Declarations which
									// match the current element
									if (styleDeclarations == null) {
										styleDeclarations = new ArrayList();
										styleDeclarations
												.add(firstStyleDeclaration);
									}
									styleDeclarations.add(wrapper);
								}
							}
						} else {
							// TODO : selector is not batik ExtendedSelector,
							// Manage this case...
						}
					}
				} else {
					// TODO : CSS rule is not ExtendedCSSRule,
					// Manage this case...
				}
			}
			}
		}
		if (styleDeclarations != null) {
			// There is several Style Declarations wich match
			// the element, merge the CSS Property value.
			return new CSSComputedStyleImpl(styleDeclarations);
		}
		if (firstStyleDeclaration != null) {
			return firstStyleDeclaration.style;
		}
		return null;
	}
}
