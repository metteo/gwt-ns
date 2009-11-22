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
	 * set the element to transform and perform any necessary setup
	 * implementations must create member variable transform.
	 * 
	 * @param elem The Element to transform
	 */
	protected abstract void initElement(Element elem);
	
	/**
	 * returns the 2 dimensional matrix transform function property per
	 * CSS3 2D Transforms Draft
	 * 
	 * specifies the current 2D transformation in the form of a transformation
	 * matrix of six values.
	 * 
	 * @see <a href="http://www.w3.org/TR/css3-2d-transforms/#transform-functions">CSS3 2D Transforms</a>
	 * 
	 * @return
	 */
	public String get2dCssString() {
		StringBuffer tmp = new StringBuffer("matrix(");
		tmp.append(transform.m11()).append(", ");
		tmp.append(transform.m21()).append(", ");
		tmp.append(transform.m12()).append(", ");
		tmp.append(transform.m22()).append(", ");
		tmp.append(transform.m14()).append(", ");
		tmp.append(transform.m24()).append(")");
		
		return tmp.toString();
	}
	
	@Override
	public void rotateLocal(double angle) {
		transform.rotateLocal(angle);
	}

	@Override
	public void rotateAtPointLocal(double angle, double px, double py) {
		transform.rotateAtPointLocal(angle, px, py);
	}

	@Override
	public void scaleLocal(double sx, double sy) {
		transform.scaleLocal(sx, sy);
	}

	@Override
	public void scaleAtPointLocal(double sx, double sy, double px, double py) {
		transform.scaleAtPointLocal(sx, sy, px, py);
	}

	@Override
	public void translateLocal(double tx, double ty) {
		transform.translateLocal(tx, ty);
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
}
