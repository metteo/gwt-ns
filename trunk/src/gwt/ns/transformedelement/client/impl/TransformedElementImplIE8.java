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

import com.google.gwt.dom.client.Document;
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

	private boolean originChanged = false;
	private double originX, originY;
	
	// may refer to transform, so change this at your peril
	private Transform originCorrected;
	private Transform originTemp;
	
	@Override
	public void commitTransform() {
		// store untransformed dimensions if we haven't already
		// element must be attached to document to have dimensions
		if (!elementInitialized && Document.get().getBody().isOrHasChild(target))
			initElementLayout();
		
		// correct for origin change. corrected (or not) now in originCorrected
		setOriginCorrectedTransform();
		
		// set linear part of transformation (2x2 matrix)
		target.getStyle().setProperty("filter", get2dFilterString());
		
		/* translation: 
		 * need to keep origin unaffected by linear transformation, only moved
		 * by translation. IE places left top corner of bounding box at
		 * (left, top). This causes shifting as bounding box changes.
		 * Find offset between where the origin (currently middle of element)
		 * should be and where it is, then subtract it from its new, translated
		 * position.
		 */
		// correct for bounding box repositioning (recenter midpoint)
		double xAdj = target.getOffsetWidth();
		xAdj = xAdj > 0 ? (xAdj - originalWidth) / 2. : 0;
		double yAdj = target.getOffsetHeight();
		yAdj = yAdj > 0 ? (yAdj - originalHeight) / 2. : 0;
		
		if (!originChanged) {
			// if origin is midpoint, just adjust for translation
			xAdj -= originCorrected.m14();
			yAdj -= originCorrected.m24();
			
		} else {
			// reposition transformed origin to origin + translation
			double ox = originX - originalWidth/2.;
			double oy = originY - originalHeight/2.;
			double tox = originCorrected.transformX(ox, oy);
			double toy = originCorrected.transformY(ox, oy);
			
			tox += target.getOffsetWidth()/2.;
			toy += target.getOffsetHeight()/2.;
			xAdj = tox - 2*originCorrected.m14();
			yAdj = toy - 2*originCorrected.m21();
		}

		// set translation from original position, transform and adjustment
		target.getStyle().setLeft(originalLeft - xAdj, Unit.PX);
		target.getStyle().setTop(originalTop - yAdj, Unit.PX);
	}


	@Override
	public void setOrigin(double ox, double oy) {
		originChanged  = true;
		originX = ox;
		originY = oy;
	}
	
	/**
	 * set originCorrected to current transform corrected for a change in origin
	 */
	protected void setOriginCorrectedTransform() {
		if (!originChanged) {
			originCorrected = transform;
			
		} else {
			double xadj = originX - originalWidth/2;
			double yadj = originY - originalHeight/2;
			
			originTemp.setToIdentity();
			originTemp.translateView(xadj, yadj); // view is faster
			originTemp.transform(transform);
			originTemp.translate(-xadj, -yadj);
			originCorrected = originTemp;
		}
	}
	
	/**
	 * Get the MsFilter string that will set the element to the current
	 * transform (2d linear transform only).
	 * 
	 * @return filter property string
	 */
	protected String get2dFilterString() {
		String filt = "progid:DXImageTransform.Microsoft.Matrix(M11=";
		filt +=	originCorrected.m11();
		filt += ", M12=" + originCorrected.m12();
		filt += ", M21=" + originCorrected.m21();
		filt += ", M22=" + originCorrected.m22();
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
		//target.getStyle().setPosition(Position.ABSOLUTE);
		target.getStyle().setLeft(0, Unit.PX);
		target.getStyle().setTop(0, Unit.PX);
		target.getStyle().setPosition(Position.ABSOLUTE);
			
		elementInitialized = true;
		originTemp = this.createTransform();
	}
}
