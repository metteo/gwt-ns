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
public class TransformedElementImplIE8 extends TransformedElement {
	protected Element target;
	protected Style targetStyle;
	
	/**
	 * The size attributes of the original element before transformation
	 */
	protected double originalWidth, originalHeight;
	
	@Override
	protected void initElement(Element elem) {
		// we can directly instantiate default transform
		transform = new TransformImplDefault();
		target = elem;
		targetStyle = target.getStyle();
		
		originalWidth = target.getOffsetWidth();
		originalHeight = target.getOffsetHeight();
		
		// TODO: this removes element from document flow altogether
		// possibly fill in dummy, hidden div to replace and add target 
		// directly to body?
		targetStyle.setProperty("position", "absolute");
		targetStyle.setProperty("top", "0");
		targetStyle.setProperty("left", "0");
	}
	
	@Override
	public void setTransform() {
		// TODO: this blows out any current filter
		// consider: "filters.item('DXImageTransform.Microsoft.Matrix')"...
		
		// set linear transformation (2x2 matrix)
		targetStyle.setProperty("filter", get2dFilterString());
		
		/* translation: 
		 * need to keep origin unaffected by linear transformation, only moved
		 * by translation. IE places top left corner of bounding box at
		 * (left, top). This causes shifting as bounding box changes.
		 * Find offset between where the origin (currently middle of element)
		 * should be and where it is, then subtract it from its new, translated
		 * position.
		 * 
		 * TODO: this is 1 of 2 places setTransformOrigin() would make its
		 * change. other before get2dFilterString() to translate coords
		 */
		double wadj = (target.getOffsetWidth()/2.) - (originalWidth/2.);
		double hadj = (target.getOffsetHeight()/2.) - (originalHeight/2.);
		
		// set translation
		targetStyle.setProperty("left", toFixed(transform.m14() - wadj, 0) + "px");
		targetStyle.setProperty("top", toFixed(transform.m24() - hadj, 0) + "px");
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
		str.append(", SizingMethod = 'auto expand')");
		
		return str.toString();
	}

	@Override
	public String get2dCssString() {
		return "";
	}
}
