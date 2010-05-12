/*
 * Copyright 2010 Brendan Kenny
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

package gwt.ns.transformedelement.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;

import gwt.ns.transforms.client.Transform;

/**
 * IE implementation of a transformed element. Currently only 2d
 * supported. 3d transforms orthographically projected to two implicitly.
 * 
 * <p>Only IE8 is currently fully supported, though IE9 Preview appears to work
 * as well. Transforms will work in IE6 and IE7, though performance will
 * drop accordingly and they are subject to quirks due to interactions
 * between the matrix filter and other style properties.</p>
 */
class TransformedElementIE8 extends TransformedElement {
	// MsFilter identity transformation
	protected static final String IDENTITY_FILTER = "progid:DXImageTransform.Microsoft.Matrix("
					+ "M11=1, M12=0, M21=0, M22=1, SizingMethod = 'auto expand')";
	
	/**
	 * Primitive-type conservatism causes some slow parts to JRE Math
	 * emulation.
	 * 
	 * @param value Number to round to nearest integer
	 * @return Rounded value
	 */
	private static native double nativeRound(double value) /*-{
		return Math.round(value);
	}-*/;
	
	// The size and position attributes of the original element
	protected double originalHeight;
	protected double originalLeft;
	protected double originalTop;
	protected double originalWidth;
	protected double halfOrigWidth;
	protected double halfOrigHeight;
	
	// true if original variables, above, are initialized
	private boolean elementInitialized = false;

	// true if user has specified a new origin
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
		double m11 = finalTransform.getA();
		m11 = m11 < 0 ? -m11 : m11; // abs()
		double m12 = finalTransform.getC();
		m12 = m12 < 0 ? -m12 : m12; // abs()
		double xAdj = ((1 - m11)*halfOrigWidth - m12*halfOrigHeight);
		
		// top adj
		double m21 = finalTransform.getB();
		m21 = m21 < 0 ? -m21 : m21; // abs()
		double m22 = finalTransform.getD();
		m22 = m22 < 0 ? -m22 : m22; // abs()
		double yAdj = (-m21*halfOrigWidth + (1 - m22)*halfOrigHeight);
		
		if (originChanged) {
			// transformed offset from top corner to current origin
			double ox = originX - halfOrigWidth;
			double oy = originY - halfOrigHeight;
			double tox = ox*finalTransform.getA() + oy*finalTransform.getC();
			double toy = ox*finalTransform.getB() + oy*finalTransform.getD();
			
			// remove from adj computed above, but orig size already figured in
			xAdj -= tox + halfOrigWidth;
			yAdj -= toy + halfOrigHeight;
		}
		
		// add translation
		xAdj += finalTransform.getE();
		yAdj += finalTransform.getF();
		
		Style tarStyle = target.getStyle();
		
		// set linear part of transformation (2x2 matrix)
		tarStyle.setProperty("filter", finalTransform.toIEFilterString());
		
		// set translation from: original position, translation, and adjustment
		// TODO: need a more sophisticated rounding scheme
		tarStyle.setProperty("left", nativeRound(originalLeft + xAdj) + "px");
		tarStyle.setProperty("top", nativeRound(originalTop + yAdj) + "px");
	}

	public void reinitializeTransform() {
		// TODO: how to trigger this?
		// content changed, etc
		elementInitialized = false;
	}
	
	@Override
	public void setOrigin(double ox, double oy) {
		if (!originChanged) {
			originTemp = Transform.create();
			originChanged  = true;
		}
		
		originX = ox;
		originY = oy;
	}
	
	/**
	 * If the origin of the current transformation has been altered, a
	 * transform is constructed by applying the current transformation about
	 * the new origin and returned. If not, the current transformation is
	 * simply returned. This returned transform is not a new object.
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
			originTemp.translate(xadj, yadj);
			originTemp.multiply(transform);
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
}
