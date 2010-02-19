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
 * Java implementation of an affine transformation using a 3x3 matrix.
 * 
 * <p><em>Note:</em> This implementation (for now) is column-major, per W3C SVG
 * specifications, so local transforms are done on the right, view transforms
 * are done on the left. This choice has been made explicit in the method
 * signatures: transforms are, by default, local transforms, unless the "view"
 * variant is selected. If internal matrix entries are accessed, however,
 * be aware that the translation vector can be found in the fourth column.</p>
 *
 * <p>Since the interface needs a 4x4 matrix, individual entry access maps to
 * an equivalent 4x4 matrix (e.g. translation is in the 4th column). This works
 * since methods to move points along the z-axis are not exposed yet, and
 * allows the interface to match the Webkit version. This also makes the
 * eventual transition to 3d transforms easy and without any breaking
 * changes.</p>
 */
public class TransformImplDefault extends Transform {
	/**
	 * Representation of current transform
	 */
	protected Matrix3x3 transform = new Matrix3x3();
	
	/**
	 * "Scratch" matrix needed for transformations.
	 * TODO: Investigate making static to save memory.
	 */
	private Matrix3x3 temp = new Matrix3x3();
	
	
	/**
	 * Construct a new transform, set to identity
	 */
	public TransformImplDefault() { }
	
	@Override
	public void rotate(double angle) {
		Matrix3x3.rotate(transform, angle);
	}

	@Override
	public void rotateAtPoint(double angle, double px, double py) {
		// TODO: optimize this to reduce matrix operations.
		// possibly refactor to combine with viewRotateAtPoint
		Matrix3x3.translate(transform, px, py);
		Matrix3x3.rotate(transform, angle);
		Matrix3x3.translate(transform, -px, -py);
	}

	@Override
	public void scale(double sx, double sy) {
		Matrix3x3.scale(transform, sx, sy);
	}

	@Override
	public void scaleAtPoint(double sx, double sy, double px, double py) {
		// TODO: optimize this to reduce matrix operations,
		// possibly refactor to combine with viewScaleAtPoint
		Matrix3x3.translate(transform, px, py);
		Matrix3x3.scale(transform, sx, sy);
		Matrix3x3.translate(transform, -px, -py);
	}

	@Override
	public void translate(double tx, double ty) {
		Matrix3x3.translate(transform, tx, ty);
	}


	@Override
	public void skewX(double angle) {
		Matrix3x3.skewX(transform, angle);
	}

	@Override
	public void skewXView(double angle) {
		Matrix3x3.identity(temp);
		Matrix3x3.skewX(temp, angle);
		
		Matrix3x3.multiplyView(transform, temp);
	}
	
	@Override
	public void skewY(double angle) {
		Matrix3x3.skewY(transform, angle);
	}

	@Override
	public void skewYView(double angle) {
		Matrix3x3.identity(temp);
		Matrix3x3.skewY(temp, angle);
		
		Matrix3x3.multiplyView(transform, temp);
	}
	
	@Override
	public void rotateView(double angle) {
		Matrix3x3.identity(temp);
		Matrix3x3.rotate(temp, angle);
		
		Matrix3x3.multiplyView(transform, temp);
	}

	@Override
	public void rotateViewAtPoint(double angle, double px, double py) {
		// TODO: optimize this to reduce matrix operations.
		// possibly refactor to combine with userRotateAtPoint
		// TODO: check order of ops
		Matrix3x3.identity(temp);
		Matrix3x3.translate(temp, px, py);
		Matrix3x3.rotate(temp, angle);
		Matrix3x3.translate(temp, -px, -py);
		
		Matrix3x3.multiplyView(transform, temp);
	}
	
	@Override
	public void scaleView(double sx, double sy) {
		transform.m11 *= sx;
		transform.m12 *= sx;
		transform.m13 *= sx;
		
		transform.m21 *= sy;
		transform.m22 *= sy;
		transform.m23 *= sy;
	}

	@Override
	public void scaleViewAtPoint(double sx, double sy, double px, double py) {
		// TODO: optimize this to reduce matrix operations,
		// possibly refactor to combine with userScaleAtPoint
		// TODO: check order of ops
		Matrix3x3.identity(temp);
		Matrix3x3.translate(temp, px, py);
		Matrix3x3.scale(temp, sx, sy);
		Matrix3x3.translate(temp, -px, -py);
		
		Matrix3x3.multiplyView(transform, temp);
	}

	@Override
	public void translateView(double tx, double ty) {
		// skip the extra matrix work
		transform.m13 += tx;
		transform.m23 += ty;
	}

	@Override
	public void setToIdentity() {
		Matrix3x3.identity(transform);
	}

	@Override
	public double m11() {
		return transform.m11;
	}

	@Override
	public double m12() {
		return transform.m12;
	}

	@Override
	public double m13() {
		return 0;
	}

	@Override
	public double m14() {
		return transform.m13;
	}

	@Override
	public double m21() {
		return transform.m21;
	}

	@Override
	public double m22() {
		return transform.m22;
	}

	@Override
	public double m23() {
		return 0;
	}

	@Override
	public double m24() {
		return transform.m23;
	}

	@Override
	public double m31() {
		return transform.m31;
	}

	@Override
	public double m32() {
		return transform.m32;
	}

	@Override
	public double m33() {
		return 0;
	}

	@Override
	public double m34() {
		return transform.m33;
	}

	@Override
	public double m41() {
		return 0;
	}

	@Override
	public double m42() {
		return 0;
	}

	@Override
	public double m43() {
		return 0;
	}

	@Override
	public double m44() {
		return 1;
	}

	@Override
	public void setM11(double m11) {
		transform.m11 = m11;
	}

	@Override
	public void setM12(double m12) {
		transform.m12 = m12;
	}

	@Override
	public void setM13(double m13) {
		;
	}

	@Override
	public void setM14(double m14) {
		transform.m13 = m14;
	}

	@Override
	public void setM21(double m21) {
		transform.m21 = m21;
	}

	@Override
	public void setM22(double m22) {
		transform.m22 = m22;
	}

	@Override
	public void setM23(double m23) {
		;
	}

	@Override
	public void setM24(double m24) {
		transform.m23 = m24;
	}

	@Override
	public void setM31(double m31) {
		transform.m31 = m31;
	}

	@Override
	public void setM32(double m32) {
		transform.m32 = m32;
	}

	@Override
	public void setM33(double m33) {
		;
	}

	@Override
	public void setM34(double m34) {
		transform.m33 = m34;
	}

	@Override
	public void setM41(double m41) {
		;
	}

	@Override
	public void setM42(double m42) {
		;
	}

	@Override
	public void setM43(double m43) {
		;
	}

	@Override
	public void setM44(double m44) {
		;
	}
}
