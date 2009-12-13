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
import com.google.gwt.dom.client.Style.Unit;

/**
 * Implementation of CSS transform for IE
 * @see <a href="http://msdn.microsoft.com/en-us/library/ms533014%28VS.85%29.aspx">Matrix Filter at MSDN</a>
 */
public class TransformedElementImplIE8 extends TransformedElement {
	/*
	 *  TODO: there are many pitfalls here:
	 *  1. accurate translation relies on originalWidth/Height being set and
	 *  	accurate which only happen when target is attached to the DOM and
	 *  	not reattached in a way that changes its layout. currently this
	 *  	class only lazily loads the original bounds once
	 *  2. moreover, these are only loaded when commitTransform() is called
	 *  	and the element is attached to the DOM. if not, lazy loading is
	 *  	delayed to the next call, something the user might not be aware of.
	 *  	Need to document this and/or change method calls to make more
	 *  	apparent.
	 *  3. Layout changes with relative positioning. Discussed elsewhere.
	 *  	reliant on the user right now to deal with layout.
	 *  4. this blows out any other current filter (including opacity etc? not
	 *  	sure)
	 */
	
	/**
	 * The size attributes of the original element before transformation
	 */
	protected double originalWidth, originalHeight;
	
	/**
	 * The position attributes of the original element before transformation
	 */
	protected double originalLeft, originalTop;
	
	/**
	 * MsFilter value string for an identity transform
	 */
	protected static final String IDENTITY_FILTER = "progid:DXImageTransform.Microsoft.Matrix("
					+ "M11=1, M12=0, M21=0, M22=1, SizingMethod = 'auto expand')";
	
	
	/**
	 * true if original* variables, above, are initialized
	 */
	protected boolean elementInitialized = false;
	
	@Override
	public void commitTransform() {
		// store untransformed dimensions if we haven't already
		// element must be attached to document to have dimensions
		if (!elementInitialized && Document.get().getBody().isOrHasChild(target))
			initElementLayout();
		
		// set linear part of transformation (2x2 matrix)
		target.getStyle().setProperty("filter", get2dFilterString());
		
		/* translation: 
		 * need to keep origin unaffected by linear transformation, only moved
		 * by translation. IE places left top corner of bounding box at
		 * (left, top). This causes shifting as bounding box changes.
		 * Find offset between where the origin (currently middle of element)
		 * should be and where it is, then subtract it from its new, translated
		 * position.
		 * 
		 * TODO: this is 1 of 2 places setTransformOrigin() would make its
		 * change. other before get2dFilterString() to translate coords
		 */
		double wAdj = target.getOffsetWidth();
		wAdj = wAdj > 0 ? (wAdj - originalWidth) / 2. : 0;
		
		double hAdj = target.getOffsetHeight();
		hAdj = hAdj > 0 ? (hAdj - originalHeight) / 2. : 0;
		
		// set translation from original position, transform and adjustment
		//String left = toFixed(originalLeft + transform.m14() - wAdj, 0) + "px";
		//String top = toFixed(originalTop + transform.m24() - hAdj, 0) + "px";
		//target.getStyle().setProperty("left", left);
		//target.getStyle().setProperty("top", top);
		target.getStyle().setLeft(originalLeft + transform.m14() - wAdj, Unit.PX);
		target.getStyle().setTop(originalTop + transform.m24() - hAdj, Unit.PX);
	}

	/**
	 * Get the MsFilter string that will set the element to the current
	 * transform (2d linear transform only).
	 * 
	 * @return filter property string
	 */
	protected String get2dFilterString() {
		String filt = "progid:DXImageTransform.Microsoft.Matrix(M11=";
		filt +=	transform.m11();
		filt += ", M12=" + transform.m12();
		filt += ", M21=" + transform.m21();
		filt += ", M22=" + transform.m22();
		filt += ", SizingMethod = 'auto expand')";
		
		return filt;
	}
	
	
	/**
	 * Apply an identity filter and store target's untransformed dimensions
	 */
	protected void initElementLayout() {
		// explicitly set an identity transform
		target.getStyle().setProperty("filter", IDENTITY_FILTER);

		// get untransfomed dimensions
		originalWidth = target.getOffsetWidth();
		originalHeight = target.getOffsetHeight();
		
		originalLeft = target.getOffsetLeft();
		originalTop = target.getOffsetTop();

		// allow to move freely within parent coord system
		//target.getStyle().setProperty("position", "absolute");
		//target.getStyle().setProperty("top", "0");
		//target.getStyle().setProperty("left", "0");
		//target.getStyle().setPosition(Position.ABSOLUTE);
		target.getStyle().setLeft(0, Unit.PX);
		target.getStyle().setTop(0, Unit.PX);
			
		elementInitialized = true;
	}
}
