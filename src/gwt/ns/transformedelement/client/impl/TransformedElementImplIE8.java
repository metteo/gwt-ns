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
import gwt.ns.transforms.client.Transform;

import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
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
	 *  	not reattached or changed in a way that changes its layout.
	 *  	currently this class only lazily loads the original bounds once.
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
	 * MsFilter identity transformation
	 */
	protected static final String IDENTITY_FILTER = "progid:DXImageTransform.Microsoft.Matrix("
					+ "M11=1, M12=0, M21=0, M22=1, SizingMethod = 'auto expand')";
	
	// static array used to construct filter string
	private static JsArrayNumber filterArray;
	
	// flag for static array intilization
	private static boolean filterArrayInited = false;
	
	/**
	 * Constructs a Matrix Filter string for the 2d linear transformation
	 * portion of the specified transform.
	 * 
	 * @param transform The transform to be applied
	 * 
	 * @return A Matrix Filter string
	 */
	protected static String get2dFilterString(Transform transform) {
		// construct only once
		if (!filterArrayInited) {
			filterArray = getFilterArray();
			filterArrayInited = true;
		}
		
		filterArray.set(1, transform.m11());
		filterArray.set(3, transform.m12());
		filterArray.set(5, transform.m21());
		filterArray.set(7, transform.m22());
		return filterArray.join("");
	}
	
	/**
	 * Creates a native array of filter string components. When odd entries are
	 * filled, array can be joined for filter string.
	 * 
	 * NOTE: breaks type safety
	 * 
	 * @return filter array
	 */
	private static native JsArrayNumber getFilterArray() /*-{
		return [
			"progid:DXImageTransform.Microsoft.Matrix(M11=",
			1,
			", M12=",
			0,
			", M21=",
			0,
			", M22=",
			1,
			", SizingMethod = 'auto expand')"
			];
	}-*/; 
	
	/**
	 * The size and position attributes of the original element
	 */
	protected double halfOrigWidth;
	protected double halfOrigHeight;
	protected double originalHeight;
	protected double originalLeft;
	protected double originalTop;
	protected double originalWidth;
	
	// true if original variables, above, are initialized
	private boolean elementInitialized = false;

	// user has specified a new origin
	private boolean originChanged = false;
	
	// temporary transform to store transformation with changed origin
	private Transform originTemp;
	
	// user-specified origin coordinates
	private double originX;
	private double originY;
	
	@Override
	public void commitTransform() {
		// store untransformed dimensions if we haven't already
		// element must be attached to document to have dimensions
		if (!elementInitialized) {
			if (Document.get().getBody().isOrHasChild(target)) {
				initElementLayout();
			} else {
				return;
			}
		}
		
		// correct for origin change. corrected (or not) returned
		Transform finalTransform = getOriginCorrectedTransform();
		
		/* translation: 
		 * need to keep origin unaffected by linear transformation, only moved
		 * by translation. IE places left top corner of bounding box at
		 * (left, top). This causes shifting as bounding box changes.
		 * Find offset between where the origin should be and where it is, then
		 * subtract it from its new, translated position.
		 */
		
		// left adj
		double m11 = finalTransform.m11();
		m11 = m11 < 0 ? -m11 : m11; // abs()
		double m12 = finalTransform.m12();
		m12 = m12 < 0 ? -m12 : m12; // abs()
		double xAdj = ((1 - m11)*halfOrigWidth - m12*halfOrigHeight);
		
		// top adj
		double m21 = finalTransform.m21();
		m21 = m21 < 0 ? -m21 : m21; // abs()
		double m22 = finalTransform.m22();
		m22 = m22 < 0 ? -m22 : m22; // abs()
		double yAdj = (-m21*halfOrigWidth + (1 - m22)*halfOrigHeight);
		
		if (originChanged) {
			// transformed offset from top corner to current origin
			double ox = originX - halfOrigWidth;
			double oy = originY - halfOrigHeight;
			double tox = ox*finalTransform.m11() + oy*finalTransform.m12();
			double toy = ox*finalTransform.m21() + oy*finalTransform.m22();
			
			// remove from adj computed above, but orig size already figured in
			xAdj -= tox + halfOrigWidth;
			yAdj -= toy + halfOrigHeight;
		}
		
		// add translation
		xAdj += finalTransform.m14();
		yAdj += finalTransform.m24();
		
		Style tarStyle = target.getStyle();
		
		// set linear part of transformation (2x2 matrix)
		tarStyle.setProperty("filter", get2dFilterString(finalTransform));
		
		// TODO:
		// seems like this would be faster, but in-IE benchmarks find it causes
		// ~30% longer execution time for entire commitTransform().
		// needs more testing
		//setFilter(originCorrected.m11(), originCorrected.m12(), originCorrected.m21(), originCorrected.m22());
		
		// set translation from: original position, translation, and adjustment
		// TODO: avoiding enum clinit. should that be happening?
		tarStyle.setProperty("left", originalLeft + xAdj + "px");
		tarStyle.setProperty("top", originalTop + yAdj + "px");
	}


	@Override
	public void setOrigin(double ox, double oy) {
		originChanged  = true;
		
		originTemp = this.createTransform();
		
		originX = ox;
		originY = oy;
	}
	
	/**
	 * If the origin of the current transformation has been altered, a
	 * transform is constructed by applying the current transformation about
	 * the new origin and returned. If not, the current transformation is
	 * simply returned.
	 * 
	 * @return The current transform corrected for a change in origin
	 */
	protected Transform getOriginCorrectedTransform() {
		if (!originChanged) {
			return transform;
			
		} else {
			double xadj = originX - originalWidth/2;
			double yadj = originY - originalHeight/2;
			
			originTemp.setToIdentity();
			originTemp.translateView(xadj, yadj); // view is faster
			originTemp.transform(transform);
			originTemp.translate(-xadj, -yadj);
			return originTemp;
		}
	}
	
	/**
	 * Apply an identity filter and store target's untransformed dimensions
	 */
	protected void initElementLayout() {
		// explicitly set an identity transform. this will:
		// a) flush out possible layout changes caused by transforms
		// b) set filter so it can be accessed by property
		target.getStyle().setProperty("filter", IDENTITY_FILTER);

		// get untransformed dimensions
		originalWidth = target.getOffsetWidth();
		halfOrigWidth = originalWidth / 2.;
		originalHeight = target.getOffsetHeight();
		halfOrigHeight = originalHeight / 2.;
		
		originalLeft = target.getOffsetLeft();
		originalTop = target.getOffsetTop();

		// allow to move freely within parent coord system
		target.getStyle().setLeft(0, Unit.PX);
		target.getStyle().setTop(0, Unit.PX);
		target.getStyle().setPosition(Position.ABSOLUTE);
		
		elementInitialized = true;
	}
	
	/**
	 * Set the matrix filter by property access. Note that the filter does not
	 * exist until it is written to once. Currently this takes place with
	 * an identity transform in {@link #initElementLayout()}.
	 * 
	 * <p>Note: this method appears to be the slower way to apply a transform
	 * to an element. It remains for more definitive testing.</p>
	 * 
	 * @param m11 The matrix entry in the 1st row, 1st column.
	 * @param m12 The matrix entry in the 1st row, 2nd column.
	 * @param m21 The matrix entry in the 2nd row, 1st column.
	 * @param m22 The matrix entry in the 2nd row, 2nd column.
	 */
	protected final native void setFilter(double m11, double m12, double m21, double m22) /*-{
		// only exists if filter has been set before (see initElementLayout)
		var matFilter = this.@gwt.ns.transformedelement.client.TransformedElement::target.filters['DXImageTransform.Microsoft.Matrix'];
		matFilter.M11 = m11;
		matFilter.M12 = m12;
		matFilter.M21 = m21;
		matFilter.M22 = m22;
	}-*/;
}