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

package gwt.ns.transforms.client.impl;

import gwt.ns.transforms.client.Transform;


/**
 * Webkit Implementation of an affine transformation using native functionality
 * of Safari 4+ and Chrome (v??) for matrix manipulations.
 * See {@link WebKitCssMatrix} for further details.<br><br>
 * 
 * <em>Note:</em> WebKitCSSMatrix treats vectors as rows (sometimes called
 * row-major ordering), so this class adjusts where necessary to convert to
 * column major. This means local transforms happen on the left and view
 * transforms happen on the right. Accessors (e.g. m11()) and setters
 * (setM11()) access and set the transpose of what is implied by the method
 * signature.
 * The result is a column-major matrix to the user, unifying
 * transformations under what will hopefully be the future standard, based on
 * the current SVG standard.<br><br>
 * 
 * The view vs. local choice has been made explicit in the method
 * signatures: transforms are, by default, local transforms, unless the "view"
 * variant is selected. If internal matrix entries are accessed,
 * be aware that the translation vector can be found in the fourth column.
 *
 */
public class TransformImplWebkit extends Transform {
	public WebKitCssMatrix transform = WebKitCssMatrix.newInstance();
	
	/**
	 * Construct a new 3D transform using webkit's native functionality,
	 * set to identity.
	 */
	public TransformImplWebkit() { }
	
	@Override
	public void rotate(double angle) {
		transform = transform.rotate(angle);
	}

	@Override
	public void rotateAtPoint(double angle, double px, double py) {
		// TODO: optimize this to reduce matrix creations and arithmetic.
		// possibly refactor to combine with viewRotateAtPoint
		transform = transform.translate(px, py).rotate(angle).translate(-px, -py);
	}

	@Override
	public void scale(double sx, double sy) {
		transform = transform.scale(sx, sy);
	}

	@Override
	public void scaleAtPoint(double sx, double sy, double px, double py) {
		// TODO: optimize this to reduce matrix creations and arithmetic,
		// possibly refactor to combine with viewScaleAtPoint
		transform = transform.translate(px, py).scale(sx, sy).translate(-px, -py);
	}
	

	@Override
	public void skewX(double angle) {
		transform = transform.skewX(angle);
	}

	@Override
	public void skewY(double angle) {
		transform = transform.skewY(angle);
	}

	@Override
	public void translate(double tx, double ty) {
		transform = transform.translate(tx, ty);

	}

	@Override
	public void rotateView(double angle) {
		WebKitCssMatrix rot = WebKitCssMatrix.newInstance().rotate(angle);
		transform = transform.multiplyView(rot);
	}

	@Override
	public void rotateViewAtPoint(double angle, double px, double py) {
		// TODO: optimize this to reduce matrix creations and arithmetic.
		// possibly refactor to combine with userRotateAtPoint
		// TODO: check the order of ops on rot.
		WebKitCssMatrix rot = WebKitCssMatrix.newInstance();
		rot = rot.translate(px, py).rotate(angle).translate(-px, -py);
		
		transform = transform.multiplyView(rot);
	}

	@Override
	public void scaleView(double sx, double sy) {
		WebKitCssMatrix scale = WebKitCssMatrix.newInstance().scale(sx, sy);
		transform = transform.multiplyView(scale);
	}

	@Override
	public void scaleViewAtPoint(double sx, double sy, double px, double py) {
		// TODO: optimize this to reduce matrix creations and arithmetic,
		// possibly refactor to combine with userScaleAtPoint
		// TODO: check the order of ops on scale.
		WebKitCssMatrix scale = WebKitCssMatrix.newInstance();
		scale = scale.translate(px, py).scale(sx, sy).translate(-px, -py);
		
		transform = transform.multiplyView(scale);
	}

	@Override
	public void translateView(double tx, double ty) {
		WebKitCssMatrix trans = WebKitCssMatrix.newInstance().translate(tx, ty);
		transform = transform.multiplyView(trans);
	}
	

	@Override
	public void skewXView(double angle) {
		WebKitCssMatrix trans = WebKitCssMatrix.newInstance().skewX(angle);
		transform = transform.multiplyView(trans);
	}

	@Override
	public void skewYView(double angle) {
		WebKitCssMatrix trans = WebKitCssMatrix.newInstance().skewY(angle);
		transform = transform.multiplyView(trans);
	}

	@Override
	public void setToIdentity() {
		transform.setToIdentity();
	}
	
	@Override
	public double m11() {
		return transform.m11();
	}

	@Override
	public double m12() {
		return transform.m21();
	}

	@Override
	public double m13() {
		return transform.m31();
	}

	@Override
	public double m14() {
		return transform.m41();
	}

	@Override
	public double m21() {
		return transform.m12();
	}

	@Override
	public double m22() {
		return transform.m22();
	}

	@Override
	public double m23() {
		return transform.m32();
	}

	@Override
	public double m24() {
		return transform.m42();
	}

	@Override
	public double m31() {
		return transform.m13();
	}

	@Override
	public double m32() {
		return transform.m23();
	}

	@Override
	public double m33() {
		return transform.m33();
	}

	@Override
	public double m34() {
		return transform.m43();
	}

	@Override
	public double m41() {
		return transform.m14();
	}

	@Override
	public double m42() {
		return transform.m24();
	}

	@Override
	public double m43() {
		return transform.m34();
	}

	@Override
	public double m44() {
		return transform.m44();
	}

	@Override
	public void setM11(double m11) {
		transform.setM11(m11);
	}

	@Override
	public void setM12(double m12) {
		transform.setM21(m12);
	}

	@Override
	public void setM13(double m13) {
		transform.setM31(m13);
	}

	@Override
	public void setM14(double m14) {
		transform.setM41(m14);
	}

	@Override
	public void setM21(double m21) {
		transform.setM12(m21);
	}

	@Override
	public void setM22(double m22) {
		transform.setM22(m22);
	}

	@Override
	public void setM23(double m23) {
		transform.setM32(m23);
	}

	@Override
	public void setM24(double m24) {
		transform.setM42(m24);
	}

	@Override
	public void setM31(double m31) {
		transform.setM13(m31);
	}

	@Override
	public void setM32(double m32) {
		transform.setM23(m32);
	}

	@Override
	public void setM33(double m33) {
		transform.setM33(m33);
	}

	@Override
	public void setM34(double m34) {
		transform.setM43(m34);
	}

	@Override
	public void setM41(double m41) {
		transform.setM14(m41);
	}

	@Override
	public void setM42(double m42) {
		transform.setM24(m42);
	}

	@Override
	public void setM43(double m43) {
		transform.setM34(m43);
	}

	@Override
	public void setM44(double m44) {
		transform.setM44(m44);
	}

}
