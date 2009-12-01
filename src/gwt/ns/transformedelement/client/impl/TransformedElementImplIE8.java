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

import com.google.gwt.dom.client.Document;

/**
 * Implementation of CSS transform for IE
 */
public class TransformedElementImplIE8 extends TransformedElement {
	/*
	 *  TODO: there are many pitfalls here:
	 *  1. accurate translation relies on originalWidth/Height being set and
	 *  	accurate which only happen when target is attached to the DOM and
	 *  	not reattached in a way that changes its layout. currently this
	 *  	class only lazily loads the original bounds once
	 *  2. moreover, these are only loaded when writeTransform() is called
	 *  	and the element is attached to the DOM. if not, lazy loading is
	 *  	delayed to the next call, something the user might not be aware of.
	 *  	Need to document this and/or change method calls to make more
	 *  	apparent.
	 *  3. Layout changes with relative positioning. Discussed elsewhere.
	 *  	reliant on the user right now to deal with layout.
	 */
	
	
	/**
	 * The size attributes of the original element before transformation
	 */
	protected double originalWidth, originalHeight;
	
	protected double originalLeft, originalTop;
	
	
	/**
	 * MsFilter value string for an identity transform
	 */
	protected static final String IDENTITY_FILTER = "progid:DXImageTransform.Microsoft.Matrix("
					+ "M11=1, M12=0, M21=0, M22=1, SizingMethod = 'auto expand')";
	
	
	protected boolean elementInitialized = false;
	
	
	/**
	 * string builder for filter string
	 */
	protected StringBuilder str = new StringBuilder();
	
	@Override
	public void commitTransform() {
		// TODO: this blows out any other current filter (including opacity etc? not sure)
		// consider: "filters.item('DXImageTransform.Microsoft.Matrix')"...
		
		// load all important untransformed dimensions if need we haven't
		// already and target is in DOM (so actually has dimensions)
		if (!elementInitialized && Document.get().getBody().isOrHasChild(target))
			initElementLayout();
		
		// set linear transformation (2x2 matrix)
		target.getStyle().setProperty("filter", get2dFilterString());
		
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
		// TODO: also further investigate this bug, when no offsetW/H, cumulative
		// 		transformations go wild
		double wadj = 0.;
		if (target.getOffsetWidth() > 0)
			wadj = (target.getOffsetWidth() - originalWidth) / 2.;
		
		double hadj = 0.;
		if (target.getOffsetHeight() > 0)
			hadj = (target.getOffsetHeight() - originalHeight) / 2.;
		
		// set translation from original position(overruled by inline style) + transform - adjustment
		// TODO: need to find a clearer flow here
		target.getStyle().setProperty("left", toFixed(originalLeft + transform.m14() - wadj, 0) + "px");
		target.getStyle().setProperty("top", toFixed(originalTop + transform.m24() - hadj, 0) + "px");
	}
	
	/**
	 * Get the MsFilter string that will set the element to the current
	 * transform (2d linear transfrom only).
	 * 
	 * @return filter property string
	 */
	protected String get2dFilterString() {
		str.delete(0, str.length());
		str.append("progid:DXImageTransform.Microsoft.Matrix(");
		str.append(  "M11=").append(transform.m11());
		str.append(", M12=").append(transform.m12());
		str.append(", M21=").append(transform.m21());
		str.append(", M22=").append(transform.m22());
		str.append(", SizingMethod = 'auto expand')");
		
		return str.toString();
	}
	
	
	protected void initElementLayout() {
		// explicitly set an identity transform
		target.getStyle().setProperty("filter", IDENTITY_FILTER);
		
		// get untransfomed dimensions
		originalWidth = target.getOffsetWidth();
		originalHeight = target.getOffsetHeight();
		
		originalLeft = target.getOffsetLeft();
		originalTop = target.getOffsetTop();

		// allow to move freely within parent coord system
		// TODO: this removes element from document flow altogether
		// possibly fill in dummy, hidden div to replace and add target 
		// to end of offsetParent?
		target.getStyle().setProperty("position", "absolute");
		target.getStyle().setProperty("top", "0");
		target.getStyle().setProperty("left", "0");
		
		elementInitialized = true;
	}
}
