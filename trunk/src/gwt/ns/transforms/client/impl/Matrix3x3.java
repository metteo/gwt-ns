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

package gwt.ns.transforms.client.impl;

/**
 * 3x3 matrix for representation of 2d affine transformations (in homogeneous
 * coordinates).
 * 
 * Most transformation methods are static to allow user to manage
 * memory usage (most methods on CSSMatrix return new matrices).
 * 
 * @see <a href="http://www.w3.org/TR/css3-2d-transforms/">W3C 2D Transforms Draft</a> 
 *
 */
public class Matrix3x3 {
	// flag for static initialization of temp matrices
	private static boolean clinit_guard = false;
	
	// Temp matrix needed for multiplication. Static since single threaded.
	private static Matrix3x3 multm;
	
	// Temp matrix needed for transformations. Static since single threaded.
	/* NOTE: rather than call identity() on it every time
	 * it's used, any method that uses it *must* return it to identity state.
	 */
	private static Matrix3x3 tempm;
	
	/**
	 * Copies matrix data from src into dest.
	 * @param dest The destination matrix
	 * @param src The source matrix
	 */
	public static final void copy(Matrix3x3 dest, Matrix3x3 src) {
		dest.m11 = src.m11; dest.m12 = src.m12; dest.m13 = src.m13;
		dest.m21 = src.m21; dest.m22 = src.m22; dest.m23 = src.m23;
		dest.m31 = src.m31; dest.m32 = src.m32; dest.m33 = src.m33;
	}
	
	/**
	 * Set a Matrix3x3 to the identity matrix.
	 * 
	 * @param target The Matrix3x3 to reset.
	 */
	public static final void identity(Matrix3x3 target) {
		target.m11 = 1; target.m12 = 0; target.m13 = 0;
		target.m21 = 0; target.m22 = 1; target.m23 = 0;
		target.m31 = 0; target.m32 = 0; target.m33 = 1;
	}
	
	/**
	 * Multiplies matrix a by b and stores the result in dest.
	 * dest can be any Matrix3x3, including a or b, which will be overwritten
	 * if specified.
	 * 
	 * <pre>dest = a*b</pre>
	 * 
	 * @param a left-hand matrix
	 * @param b right-hand matrix
	 * @param dest result of multiplication
	 */
	public static final void multiply(Matrix3x3 a, Matrix3x3 b, Matrix3x3 dest) {
		// results stored in multm scratch matrix
		multm.m11 = a.m11*b.m11 + a.m12*b.m21 + a.m13*b.m31;
		multm.m12 = a.m11*b.m12 + a.m12*b.m22 + a.m13*b.m32;
		multm.m13 = a.m11*b.m13 + a.m12*b.m23 + a.m13*b.m33;
		
		multm.m21 = a.m21*b.m11 + a.m22*b.m21 + a.m23*b.m31;
		multm.m22 = a.m21*b.m12 + a.m22*b.m22 + a.m23*b.m32;
		multm.m23 = a.m21*b.m13 + a.m22*b.m23 + a.m23*b.m33;
		
		multm.m31 = a.m31*b.m11 + a.m32*b.m21 + a.m33*b.m31;
		multm.m32 = a.m31*b.m12 + a.m32*b.m22 + a.m33*b.m32;
		multm.m33 = a.m31*b.m13 + a.m32*b.m23 + a.m33*b.m33;
		
		// and copied to dest
		copy(dest, multm);
	}
	
	/**
	 * Applies a transform in <em>local</em> coordinates to the target matrix.
	 * The transform matrix is not altered.
	 * 
	 * @param target The matrix to apply the transform to.
	 * @param transform The transform to apply.
	 */
	public static final void multiplyLocal(Matrix3x3 target, Matrix3x3 transform) {
		multiply(target, transform, target);
	}
	
	/**
	 * Applies a transform in <em>view</em> coordinates to the target matrix.
	 * The transform matrix is not altered.
	 * 
	 * @param target Matrix to apply the transform to, in view coordinates
	 * @param transform The transform to apply
	 */
	public static final void multiplyView(Matrix3x3 target, Matrix3x3 transform) {
		multiply(transform, target, target);
	}
	
	/**
	 * Rotates matrix target by angle in <em>local</em> coordinates.<br><br>
	 * 
	 * <strong>Note:</strong> due to the implicit viewport transform (+y points
	 * down on the screen), positive angles will rotate clockwise on screen.
	 * 
	 * @param theta The angle of rotation, in radians.
	 * @param target The matrix that is to be rotated
	 */
	public static final void rotate(Matrix3x3 target, double theta) {
		double cos = Math.cos(theta);
		double sin = Math.sin(theta);
		
		tempm.m11 = cos;
		tempm.m12 = -sin;
		tempm.m21 = sin;
		tempm.m22 = cos;
		
		multiplyLocal(target, tempm);
		
		// return tempm to identity state
		tempm.m11 = 1;
		tempm.m12 = 0;
		tempm.m21 = 0;
		tempm.m22 = 1;
	}
	
	/**
	 * Scales target matrix by the given vector in <em>local</em> coordinates
	 * 
	 * @param target The matrix that is to be scaled.
	 * @param sx The x component in the scale vector.
	 * @param sy The y component in the scale vector.
	 */
	public static final void scale(Matrix3x3 target, double sx, double sy) {
		tempm.m11 = sx;
		tempm.m22 = sy;
		
		multiplyLocal(target, tempm);
		
		// return tempm to identity state
		tempm.m11 = 1;
		tempm.m22 = 1;
	}
	
	/**
	 * Skews the target matrix around the x-axis by the given angle, in
	 * <em>local</em> coordinates.
	 * 
	 * @param target The matrix that is to be skewed.
	 * @param theta The angle of the skew, in radians.
	 */
	public static final void skewX(Matrix3x3 target, double theta) {
		tempm.m12 = Math.tan(theta);
		
		multiplyLocal(target, tempm);
		
		// return tempm to identity state
		tempm.m12 = 0;
	}
	
	/**
	 * Skews the target matrix around the y-axis by the given angle, in
	 * <em>local</em> coordinates.
	 * 
	 * @param target The matrix that is to be skewed.
	 * @param theta The angle of the skew, in radians.
	 */
	public static final void skewY(Matrix3x3 target, double theta) {
		tempm.m21 = Math.tan(theta);
		
		multiplyLocal(target, tempm);
		
		// return tempm to identity state
		tempm.m21 = 0;
	}
	
	/**
	 * Translates the target matrix by a given vector, in <em>local</em>
	 * coordinates.
	 * 
	 * @param target The matrix that is to be translated.
	 * @param tx The x component in the vector.
	 * @param ty The y component in the vector.
	 */
	public static final void translate(Matrix3x3 target, double tx, double ty) {
		tempm.m13 = tx;
		tempm.m23 = ty;
		
		multiplyLocal(target, tempm);
		
		// return tempm to identity state
		tempm.m13 = 0;
		tempm.m23 = 0;
	}
	
	/*
	 * Internal representation of a matrix entry.
	 * First number is row number, second is column number.
	 * Set to identity to avoid constructor/clinit() in static versions
	 */
	protected double m11 = 1.;
	protected double m21 = 0.;
	protected double m31 = 0.;
	protected double m12 = 0.;
	protected double m22 = 1.;
	protected double m32 = 0.;
	protected double m13 = 0.;
	protected double m23 = 0.;
	protected double m33 = 1.;
	
	/**
	 * Construct a new 3x3 matrix set to identity
	 */
	public Matrix3x3() {
		// hackery to have static temp matrices but avoid clinits
		// TODO: is there a better way?
		if (!clinit_guard) {
			clinit_guard = true;
			multm = new Matrix3x3();
			tempm = new Matrix3x3();
		}
	}
}
