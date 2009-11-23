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
 * Implementation of CSS transform for IE
 */
public class TransformedElementImplIE extends TransformedElement {
	protected Element target;
	protected Style targetStyle;
	
	@Override
	protected void initElement(Element elem) {
		// we can directly instantiate default transform
		// TODO: IE dom tree mangling for translations
		transform = new TransformImplDefault();
		target = elem;
		targetStyle = target.getStyle();
	}

	@Override
	public void setTransform() {
		// TODO: this blows out any current filter
		// consider: "filters.item('DXImageTransform.Microsoft.Matrix')"
		targetStyle.setProperty("filter", get2dFilterString());
	}
	
	/**
	 * get the MsFilter string that will set the element to the current
	 * transform
	 * 
	 * @return filter property string
	 */
	protected String get2dFilterString() {
		StringBuilder str = new StringBuilder("progid:DXImageTransform.Microsoft.Matrix(");
		str.append(  "M11=").append(transform.m11());
		str.append(", M12=").append(transform.m12());
		str.append(", M21=").append(transform.m21());
		str.append(", M22=").append(transform.m22());
		str.append(", DX=").append(transform.m14());
		str.append(", DY=").append(transform.m24());
		str.append(", SizingMethod = 'auto expand')");
		
		return str.toString();
	}

	@Override
	public String get2dCssString() {
		return "";
	}

}
