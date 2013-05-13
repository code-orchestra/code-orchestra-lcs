/*

   Copyright 2002  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */

/* This class copied from org.apache.batik.css.engine.sac */

package org.eclipse.e4.ui.css.core.impl.sac;

import org.w3c.dom.Element;

/**
 * This class provides an implementation of the
 * {@link org.w3c.css.sac.AttributeCondition} interface.
 */
public class CSSOneOfAttributeConditionImpl extends CSSAttributeConditionImpl {
	/**
	 * Creates a new CSSAttributeCondition object.
	 */
	public CSSOneOfAttributeConditionImpl(String localName,
			String namespaceURI, boolean specified, String value) {
		super(localName, namespaceURI, specified, value);
	}

	/**
	 * <b>SAC</b>: Implements {@link
	 * org.w3c.css.sac.Condition#getConditionType()}.
	 */
	public short getConditionType() {
		return SAC_ONE_OF_ATTRIBUTE_CONDITION;
	}

	/**
	 * Tests whether this condition matches the given element.
	 */
	public boolean match(Element e, String pseudoE) {
		String attr = e.getAttribute(getLocalName());
		String val = getValue();
		int i = attr.indexOf(val);
		if (i == -1) {
			return false;
		}
		if (i != 0 && !Character.isSpaceChar(attr.charAt(i - 1))) {
			return false;
		}
		int j = i + val.length();
		return (j == attr.length() || (j < attr.length() && Character
				.isSpaceChar(attr.charAt(j))));
	}

	/**
	 * Returns a text representation of this object.
	 */
	public String toString() {
		return "[" + getLocalName() + "~=\"" + getValue() + "\"]";
	}
}
