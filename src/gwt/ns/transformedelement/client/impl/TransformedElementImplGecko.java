/*
 * Copyright 2009 Brendan Kenny
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gwt.ns.transformedelement.client.impl;

import gwt.ns.transformedelement.client.TransformedElement;
import gwt.ns.transforms.client.impl.TransformImplDefault;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;

/**
 * Implementation of CSS transform for gecko browsers
 * will only work in 1.9.1 up to when standard is implemented
 */
public class TransformedElementImplGecko extends TransformedElement {
	protected Element target;
	protected Style targetStyle;
	
	@Override
	protected void initElement(Element elem) {
		// we can directly instantiate default transform
		transform = new TransformImplDefault();
		target = elem;
		targetStyle = target.getStyle();
	}

	@Override
	public void setTransform() {
		targetStyle.setProperty("MozTransform", get2dCssString());
	}

}
