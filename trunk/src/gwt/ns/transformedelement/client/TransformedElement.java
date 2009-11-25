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

package gwt.ns.transformedelement.client;

import gwt.ns.transforms.client.Transform;
import gwt.ns.transforms.client.Transformable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;


public abstract class TransformedElement implements Transformable {
	// TODO transformable interface just convenience right now...might be unnecessary
	// TODO: better class name to reflect functionality
	// TODO: implement as concrete class implementing standard
	// TODO: changeOrigin()?
	// TODO: transition?
	// TODO: units?? ems, px, cm allowed in firefox at least...
	// TODO: set from current element? or from element being wrapped?
		// this would allow to wrap an element that already has a transform
	// TODO: setToIdentity vs reset to initial state? (see above)
	
	protected Transform transform;
	
	protected static final int STYLE_PRECISION = 10;
	 
	protected TransformedElement() { }
	
	public static TransformedElement wrap(Element elem) {
		// assert that the element is attached
		assert Document.get().getBody().isOrHasChild(elem);
		
		// get a system appropriate implementation of TransformedElement
		TransformedElement transElem = (TransformedElement) GWT.create(TransformedElement.class);
		transElem.initElement(elem);
		
		return transElem;
	}
	
	/**
	 * apply the current transform to element
	 * Note: this involves DOM access and style setting, so might be slow
	 */
	public abstract void setTransform();
	
	/**
	 * Set the element to transform and perform any necessary setup.
	 * Implementations must create member variable transform.
	 * 
	 * @param elem The Element to transform
	 */
	protected abstract void initElement(Element elem);
	
	/**
	 * Returns the 2 dimensional matrix transform function property per
	 * CSS3 2D Transforms Draft<br><br>
	 * 
	 * Specifies the current 2D transformation in the form of an augmented
	 * 2x2 transformation matrix and translation vector.<br><br>
	 * 
	 * It's not completely clear who will prevail on the subject of a length unit for
	 * the translation vector. It makes sense in other contexts, but doesn't make
	 * complete sense in the midst of a bunch of other unitless numbers.<br><br>
	 * 
	 * Regardless, currently, firefox needs a unit, webkit does not.<br><br>
	 * 
	 * @see <a href="http://www.w3.org/TR/css3-2d-transforms/#transform-functions">CSS3 2D Transforms</a>
	 * 
	 * @return
	 */
	public abstract String get2dCssString();
	
	/**
	 * not ideal, but lightweight. convert a floating point number to a string
	 * with the specified number of digits after the decimal place (note:
	 * that's not *total* digits)
	 * 
	 * @param value to round and convert
	 * @param numDigits	number of digits after the decimal point
	 * @return
	 */
	protected final native String toFixed(double value, int numDigits) /*-{
		return value.toFixed(numDigits);
	}-*/;
	
	protected String toFixed(double value) {
		return toFixed(value, STYLE_PRECISION);
	}
	
	@Override
	public void rotate(double angle) {
		transform.rotate(angle);
	}

	@Override
	public void rotateAtPoint(double angle, double px, double py) {
		transform.rotateAtPoint(angle, px, py);
	}

	@Override
	public void scale(double sx, double sy) {
		transform.scale(sx, sy);
	}

	@Override
	public void scaleAtPoint(double sx, double sy, double px, double py) {
		transform.scaleAtPoint(sx, sy, px, py);
	}

	@Override
	public void translate(double tx, double ty) {
		transform.translate(tx, ty);
	}

	@Override
	public void rotateView(double angle) {
		transform.rotateView(angle);
	}

	@Override
	public void rotateAtPointView(double angle, double px, double py) {
		transform.rotateAtPointView(angle, px, py);
	}

	@Override
	public void scaleView(double sx, double sy) {
		transform.scaleView(sx, sy);
	}

	@Override
	public void scaleAtPointView(double sx, double sy, double px, double py) {
		transform.scaleAtPointView(sx, sy, px, py);
	}

	@Override
	public void translateView(double tx, double ty) {
		transform.translateView(tx, ty);
	}

	@Override
	public void reset() {
		transform.reset();
	}

	@Override
	public void skewX(double angle) {
		transform.skewX(angle);
	}

	@Override
	public void skewY(double angle) {
		transform.skewY(angle);
	}
	
	@Override
	public void skewXView(double angle) {
		transform.skewXView(angle);
	}

	@Override
	public void skewYView(double angle) {
		transform.skewYView(angle);
	}
}
