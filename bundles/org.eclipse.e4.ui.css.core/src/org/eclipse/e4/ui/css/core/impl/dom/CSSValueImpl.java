/*******************************************************************************
 * Copyright (c) 2008, 2010 Angelo Zerr and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/

package org.eclipse.e4.ui.css.core.impl.dom;

import java.io.Serializable;
import org.eclipse.e4.ui.css.core.exceptions.DOMExceptionImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.Counter;
import org.w3c.dom.css.RGBColor;
import org.w3c.dom.css.Rect;

public abstract class CSSValueImpl extends AbstractCSSNode implements CSSPrimitiveValue, CSSValue, Serializable {

	// W3C CSSValue API methods
	
	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.css.CSSValue#getCssText()
	 */
	public String getCssText() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.css.CSSValue#getCssValueType()
	 */
	public short getCssValueType() {
		return CSS_PRIMITIVE_VALUE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.css.CSSValue#setCssText(java.lang.String)
	 */
	public void setCssText(String cssText) throws DOMException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	// W3C CSSPrimitiveValue API methods

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.css.CSSPrimitiveValue#getPrimitiveType()
	 */
	public short getPrimitiveType() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.css.CSSPrimitiveValue#getCounterValue()
	 */
	public Counter getCounterValue() throws DOMException {
		throw new DOMExceptionImpl(DOMException.INVALID_ACCESS_ERR, DOMExceptionImpl.COUNTER_ERROR);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.css.CSSPrimitiveValue#getRGBColorValue()
	 */
	public RGBColor getRGBColorValue() throws DOMException {
		throw new DOMExceptionImpl(DOMException.INVALID_ACCESS_ERR, DOMExceptionImpl.RGBCOLOR_ERROR);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.css.CSSPrimitiveValue#getRectValue()
	 */
	public Rect getRectValue() throws DOMException {
		throw new DOMExceptionImpl(DOMException.INVALID_ACCESS_ERR, DOMExceptionImpl.RECT_ERROR);
	}

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.css.CSSPrimitiveValue#getStringValue()
	 */
	public String getStringValue() throws DOMException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.css.CSSPrimitiveValue#setFloatValue(short, float)
	 */
	public void setFloatValue(short arg0, float arg1) throws DOMException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.css.CSSPrimitiveValue#setStringValue(short, java.lang.String)
	 */
	public void setStringValue(short arg0, String arg1) throws DOMException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}
	
	// Additional methods
	
	public float getFloatValue(short valueType) throws DOMException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}


}