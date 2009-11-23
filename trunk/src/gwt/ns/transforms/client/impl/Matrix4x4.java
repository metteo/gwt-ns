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

/**
 * 4x4 matrix for representation of affine transformations (in homogenous
 * coordinates). Interfaces for 2d and 3d transformations based on CSSMatrix
 * 
 * Most transformation methods are static to allow user to manage
 * memory usage (most methods on CSSMatrix return new matrices).
 * Note that this, at least for now, precludes relying on e.g. webkit's native
 * implementation, and thus hardware acceleration(?) on some platforms
 * 
 * @see <a href="http://www.w3.org/TR/css3-2d-transforms/">W3C 2D Transforms Draft</a> 
 * @see <a href="http://www.w3.org/TR/css3-3d-transforms/">W3C 3D Transforms Draft</a>
 *
 */
public class Matrix4x4 {
	/**
	 * Internal representation of a matrix entry.
	 * First number is row number, second is column number.
	 */
	protected double m11, m21, m31, m41, m12, m22, m32, m42, m13, m23, m33,
					m43, m14, m24, m34, m44;
	
	/**
	 * "Scratch" matrix needed for multiplication. Declared static since we are
	 * single threaded.
	 */
	private static Matrix4x4 multm = new Matrix4x4();
	
	/**
	 * "Scratch" matrix needed for transformations. Declared static since we
	 * are single threaded.
	 */
	private static Matrix4x4 tempm = new Matrix4x4();
	
	/**
	 * Construct a new 4x4 matrix set to identity
	 */
	public Matrix4x4() {
		identity(this);
	}
	
	/**
	 * Multiplies matrix a by b and stores the result in dest.
	 * dest can be any Matrix4x4, including a or b, which will be overwritten
	 * if specified.
	 * 
	 * <pre>dest = a*b</pre>
	 * 
	 * @param a left-hand matrix
	 * @param b right-hand matrix
	 * @param dest result of multiplication
	 */
	public static final void multiply(Matrix4x4 a, Matrix4x4 b, Matrix4x4 dest) {
		// results stored in multm scratch matrix
		multm.m11 = a.m11*b.m11 + a.m12*b.m21 + a.m13*b.m31 + a.m14*b.m41;
		multm.m12 = a.m11*b.m12 + a.m12*b.m22 + a.m13*b.m32 + a.m14*b.m42;
		multm.m13 = a.m11*b.m13 + a.m12*b.m23 + a.m13*b.m33 + a.m14*b.m43;
		multm.m14 = a.m11*b.m14 + a.m12*b.m24 + a.m13*b.m34 + a.m14*b.m44;
		
		multm.m21 = a.m21*b.m11 + a.m22*b.m21 + a.m23*b.m31 + a.m24*b.m41;
		multm.m22 = a.m21*b.m12 + a.m22*b.m22 + a.m23*b.m32 + a.m24*b.m42;
		multm.m23 = a.m21*b.m13 + a.m22*b.m23 + a.m23*b.m33 + a.m24*b.m43;
		multm.m24 = a.m21*b.m14 + a.m22*b.m24 + a.m23*b.m34 + a.m24*b.m44;
		
		multm.m31 = a.m31*b.m11 + a.m32*b.m21 + a.m33*b.m31 + a.m34*b.m41;
		multm.m32 = a.m31*b.m12 + a.m32*b.m22 + a.m33*b.m32 + a.m34*b.m41;
		multm.m33 = a.m31*b.m13 + a.m32*b.m23 + a.m33*b.m33 + a.m34*b.m41;
		multm.m34 = a.m31*b.m14 + a.m32*b.m24 + a.m33*b.m34 + a.m34*b.m44;
		
		multm.m41 = a.m41*b.m11 + a.m42*b.m21 + a.m43*b.m31 + a.m44*b.m41;
		multm.m42 = a.m41*b.m12 + a.m42*b.m22 + a.m43*b.m32 + a.m44*b.m41;
		multm.m43 = a.m41*b.m13 + a.m42*b.m23 + a.m43*b.m33 + a.m44*b.m41;
		multm.m44 = a.m41*b.m14 + a.m42*b.m24 + a.m43*b.m34 + a.m44*b.m44;
		
		// and copied to dest
		copy(multm, dest);
	}
	
	/**
	 * Rotates matrix target by angle in <em>local</em> coordinates.<br><br>
	 * 
	 * <strong>Note:</strong> due to the implicit viewport transform (+y points
	 * down on the screen), positive angles rotate clockwise.
	 * 
	 * @param angle The angle of rotation, in degrees.
	 * @param target The matrix that is to be rotated
	 */
	public static final void rotate(Matrix4x4 target, double angle) {
		angle = Math.toRadians(angle);
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		
		identity(tempm);
		tempm.m11 = cos;
		tempm.m12 = -sin;
		tempm.m21 = sin;
		tempm.m22 = cos;
		
		multiplyLocal(target, tempm);
	}
	/**
	 * Scales target matrix by the given vector in <em>local</em> coordinates
	 * 
	 * @param target The matrix that is to be scaled.
	 * @param sx The x component in the scale vector.
	 * @param sy The y component in the scale vector.
	 */
	public static final void scale(Matrix4x4 target, double sx, double sy) {
		identity(tempm);
		tempm.m11 = sx;
		tempm.m22 = sy;
		
		multiplyLocal(target, tempm);
	}
	
	/**
	 * Translates the target matrix by a given vector, in <em>local</em>
	 * coordinates.
	 * 
	 * @param target The matrix that is to be translated.
	 * @param tx The x component in the vector.
	 * @param ty The y component in the vector.
	 */
	public static final void translate(Matrix4x4 target, double tx, double ty) {
		identity(tempm);
		tempm.m14 = tx;
		tempm.m24 = ty;
		
		multiplyLocal(target, tempm);
	}
	
	/**
	 * Skews the target matrix around the x-axis by the given angle, in
	 * <em>local</em> coordinates.
	 * 
	 * @param target The matrix that is to be skewed.
	 * @param angle The angle of the skew.
	 */
	public static final void skewX(Matrix4x4 target, double angle) {
		identity(tempm);
		tempm.m12 = Math.tan(Math.toRadians(angle));
		
		multiplyLocal(target, tempm);
	}
	
	/**
	 * Skews the target matrix around the y-axis by the given angle, in
	 * <em>local</em> coordinates.
	 * 
	 * @param target The matrix that is to be skewed.
	 * @param angle The angle of the skew.
	 */
	public static final void skewY(Matrix4x4 target, double angle) {
		identity(tempm);
		tempm.m21 = Math.tan(Math.toRadians(angle));
		
		multiplyLocal(target, tempm);
	}
	
	/**
	 * Copies matrix data from src into dest.
	 * 
	 * @param src The source matrix
	 * @param dest The destination matrix
	 */
	public static final void copy(Matrix4x4 src, Matrix4x4 dest) {
		dest.m11 = src.m11; dest.m12 = src.m12; dest.m13 = src.m13; dest.m14 = src.m14;
		dest.m21 = src.m21; dest.m22 = src.m22; dest.m23 = src.m23; dest.m24 = src.m24;
		dest.m31 = src.m31; dest.m32 = src.m32; dest.m33 = src.m33; dest.m34 = src.m34;
		dest.m41 = src.m41; dest.m42 = src.m42; dest.m43 = src.m43; dest.m44 = src.m44;
	}
	
	/**
	 * Set a Matrix4x4 to the identity matrix.
	 * 
	 * @param target The Matrix4x4 to reset.
	 */
	public static final void identity(Matrix4x4 target) {
		target.m11 = 1; target.m12 = 0; target.m13 = 0; target.m14 = 0;
		target.m21 = 0; target.m22 = 1; target.m23 = 0; target.m24 = 0;
		target.m31 = 0; target.m32 = 0; target.m33 = 1; target.m34 = 0;
		target.m41 = 0; target.m42 = 0; target.m43 = 0; target.m44 = 1;
	}
	
	/**
	 * Applies a transform in <em>local</em> coordinates to the target matrix.
	 * 
	 * @param target The matrix to apply the transform to.
	 * @param transform The transform to apply.
	 */
	public static final void multiplyLocal(Matrix4x4 target, Matrix4x4 transform) {
		multiply(target, transform, target);
	}
	
	/**
	 * Applies a transform in <em>view</em> coordinates to the target matrix.
	 * 
	 * @param target Matrix to apply the transform to, in view coordinates
	 * @param transform The transform to apply
	 */
	public static final void multiplyView(Matrix4x4 target, Matrix4x4 transform) {
		multiply(transform, target, target);
	}
	
}
