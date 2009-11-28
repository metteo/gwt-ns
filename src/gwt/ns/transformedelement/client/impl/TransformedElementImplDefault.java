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

/**
 * Implementation of TransformableElement for browsers with no 2d css transforms
 * For now, do nothing.
 * TODO: maybe add translations with method similar to IE?
 */
public class TransformedElementImplDefault extends TransformedElement {

	@Override
	public void writeTransform() { }
	
	// save some cycles by really doing nothing
	@Override
	public void resetTranform() { }
	
	@Override
	public void rotate(double angle) { }

	@Override
	public void rotateAtPoint(double angle, double px, double py) { }

	@Override
	public void rotateAtPointView(double angle, double px, double py) { }
	
	@Override
	public void rotateView(double angle) { }

	@Override
	public void scale(double sx, double sy) { }

	@Override
	public void scaleAtPoint(double sx, double sy, double px, double py) { }

	@Override
	public void scaleAtPointView(double sx, double sy, double px, double py) { }

	@Override
	public void scaleView(double sx, double sy) { }

	@Override
	public void setToIdentity() { }

	@Override
	public void setTransform(double t11, double t21, double t31, double t41,
			double t12, double t22, double t32, double t42, double t13,
			double t23, double t33, double t43, double t14, double t24,
			double t34, double t44) { }

	@Override
	public void setTransform(Transform transfrom) { }

	@Override
	public void skewX(double angle) { }

	@Override
	public void skewXView(double angle) { }

	@Override
	public void skewY(double angle) { }

	@Override
	public void skewYView(double angle) { }

	@Override
	public void transform(Transform transform) { }

	@Override
	public void transformView(Transform transform) { }

	@Override
	public double transformX(double x, double y) { 
		return 0;
	}

	@Override
	public double transformY(double x, double y) { 
		return 0;
	}

	@Override
	public void translate(double tx, double ty) { }

	@Override
	public void translateView(double tx, double ty) { }
}
