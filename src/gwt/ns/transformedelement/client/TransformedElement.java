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
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

public abstract class TransformedElement implements Transformable {
	// TODO: implement as concrete class implementing standard
	// TODO: changeOrigin()?
	// TODO: transition?
	// TODO: units?? ems, px, cm allowed in firefox at least...
	// TODO: setToIdentity vs reset to initial state? (see above)
	/*
	 * TODO: right now I think the ideal would be to extend a (platform-
	 * specific, deferred-bound) Transform with an added Element target
	 * and writeTransform() method. Much of the rest of this is just wrapper
	 * boilerplate. This will require a generator, however, which can be
	 * difficult to maintain for an api that's still in flux. I'm looking into
	 * using GIN, but so far I don't see a big win over what follows.
	 */
	
	protected static final int STYLE_PRECISION = 10;
	protected Element target;
	protected Transform transform;
	
	/**
	 * Apply the current transform to this Element.
	 * Note: this involves DOM access and style setting, so might be slow.
	 * TODO: investigate string creation on each platform as this is a hotspot
	 */
	public abstract void commitTransform();
	
	public static TransformedElement wrap(Element elem) {
		// get a system appropriate implementation of TransformableElement
		TransformedElement transElem = (TransformedElement) GWT.create(TransformedElement.class);
		
		// allow transforms module to handle binding
		transElem.transform = transElem.createTransform();
		
		transElem.target = elem;
		
		// TODO: set transform from element being wrapped
		// add Transform originalTranform.setTranform(css string)
		// when reset is called, setTranform(originalTransform);
		
		return transElem;
	}
	
	protected Transform createTransform() {
		// allow transforms module to handle binding
		return (Transform) GWT.create(Transform.class);
	}
	
	public static TransformedElement create() {
		return wrap(DOM.createDiv());
	}
	
	public Element getElement() {
		return target;
	}

	/**
	 * Reset object to original transformation.
	 * <br><br>
	 * Currently resets to identity.<br>
	 * TODO: if target was originally wrapped, set transform to whatever
	 * 	transform was originally set
	 */
	public void resetTranform() {
		transform.setToIdentity();
	}
	
	/**
	 * Not ideal, but lightweight. All the transform implementations but IE's
	 * won't take exponent notation, so this seems like the fastest way to
	 * format the matrix entries in decimal form.<br><br>
	 * 
	 * Convert a floating point number to a string with the specified number
	 * of digits after the decimal place (note: that is <em>not</em> total
	 * digits).
	 * 
	 * @param value to round and convert
	 * @param numDigits	number of digits after the decimal point
	 * @return String representation of value
	 */
	public static final native String toFixed(double value, int numDigits) /*-{
		return value.toFixed(numDigits);
	}-*/;
	

	public static final String toFixed(double value) {
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
	public void rotateViewAtPoint(double angle, double px, double py) {
		transform.rotateViewAtPoint(angle, px, py);
	}

	@Override
	public void rotateView(double angle) {
		transform.rotateView(angle);
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
	public void scaleViewAtPoint(double sx, double sy, double px, double py) {
		transform.scaleViewAtPoint(sx, sy, px, py);
	}

	@Override
	public void scaleView(double sx, double sy) {
		transform.scaleView(sx, sy);
	}

	@Override
	public void setToIdentity() {
		transform.setToIdentity();
	}

	@Override
	public void setTransform(double t11, double t21, double t31, double t41,
			double t12, double t22, double t32, double t42, double t13,
			double t23, double t33, double t43, double t14, double t24,
			double t34, double t44) {
		
		transform.setTransform(t11, t21, t31, t41, t12, t22, t32, t42, t13,
				t23, t33, t43, t14, t24, t34, t44);
	}

	@Override
	public void setTransform(Transform transfrom) {
		this.transform.setTransform(transfrom);
	}

	@Override
	public void skewX(double angle) {
		transform.skewX(angle);
	}

	@Override
	public void skewXView(double angle) {
		transform.skewXView(angle);
	}

	@Override
	public void skewY(double angle) {
		transform.skewY(angle);
	}

	@Override
	public void skewYView(double angle) {
		transform.skewYView(angle);
	}

	@Override
	public void transform(Transform transform) {
		this.transform.transform(transform);	// I need a thesaurus
	}

	@Override
	public void transformView(Transform transform) {
		this.transform.transformView(transform);
	}

	@Override
	public double transformX(double x, double y) {
		return transform.transformX(x, y);
	}

	@Override
	public double transformY(double x, double y) {
		return transform.transformY(x, y);
	}

	@Override
	public void translate(double tx, double ty) {
		transform.translate(tx, ty);
	}

	@Override
	public void translateView(double tx, double ty) {
		transform.translateView(tx, ty);
	}
}