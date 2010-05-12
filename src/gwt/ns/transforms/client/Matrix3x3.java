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

package gwt.ns.transforms.client;

/**
 * A 3x3, column-major matrix implementation.
 */
public class Matrix3x3 {
	private static boolean clinit_guard = false;
	
	private static Matrix3x3 multm;
	
	/**
	 * Internal representation of a matrix entry.
	 * First number is row number, second is column number.
	 */
	public double m11, m21, m31, m12, m22, m32, m13, m23, m33;
	
	/**
	 * Construct a new 3x3 matrix set to identity
	 */
	public Matrix3x3() {
		// hackery to have static temp matrices but avoid clinits
		// TODO: is there a better way?
		if (!clinit_guard) {
			clinit_guard = true;
			multm = new Matrix3x3();
		}
		
		setToIdentity();
	}
	
	public void copy(Matrix3x3 src) {
		m11 = src.m11; m12 = src.m12; m13 = src.m13;
		m21 = src.m21; m22 = src.m22; m23 = src.m23;
		m31 = src.m31; m32 = src.m32; m33 = src.m33;
	}
	
	public double determinant() {
		return m11*m22*m33 - m11*m23*m32 + m12*m23*m31 - m12*m21*m33 + m13*m21*m32 - m13*m22*m31;
	}
	
	public void inverse(Matrix3x3 dest) {
		dest.m11 = -m23*m32 + m22*m33;
		dest.m12 =  m13*m32 - m12*m33;
		dest.m13 = -m13*m22 + m12*m23;
		
		dest.m21 =  m23*m31 - m21*m33;
		dest.m22 = -m13*m31 + m11*m33;
		dest.m23 =  m13*m21 - m11*m23;
		
		dest.m31 = -m22*m31 + m21*m32;
		dest.m32 =  m12*m31 - m11*m32;
		dest.m33 = -m12*m21 + m11*m22;
		
		double det = determinant();
		if (Math.abs(det) < 1.e-8) {
			// TODO: an exception makes more sense than an assertion here
			// but not exactly parallel to JavaScriptException thrown by native
			// code. what to do?
			throw new RuntimeException("Matrix is singular");
		
		} else {
			det = 1. / det;
			dest.m11 *= det;
			dest.m12 *= det;
			dest.m13 *= det;
			dest.m21 *= det;
			dest.m22 *= det;
			dest.m23 *= det;
			dest.m31 *= det;
			dest.m32 *= det;
			dest.m33 *= det;
		}
	}
	
	public void multiply(Matrix3x3 local) {
		multm.multiply(this, local);
		copy(multm);
	}
	
	/**
	 * Multiplies matrix view by local and stores the result in this matrix.
	 * This matrix is overwritten in process of multiplication, so should not
	 * itself be view or local.
	 * 
	 * <p>Note: this is the most efficient of the multiply methods, because
	 * no copy is required, but use the transformation-specific methods if at
	 * all possible. They are each optimized for their particular
	 * operation, which can result in needing only a small fraction of the
	 * calculations needed for this general method.</p>
	 * 
	 * <pre>this = view*local</pre>
	 * 
	 * @param view left-hand matrix
	 * @param local right-hand matrix
	 */
	public void multiply(Matrix3x3 view, Matrix3x3 local) {
		// TODO: is this still the most descriptive name for this method?
		assert (this != view && this != local) : "destination matrix should not be one of the factors";
		
		m11 = view.m11*local.m11 + view.m12*local.m21 + view.m13*local.m31;
		m12 = view.m11*local.m12 + view.m12*local.m22 + view.m13*local.m32;
		m13 = view.m11*local.m13 + view.m12*local.m23 + view.m13*local.m33;
		
		m21 = view.m21*local.m11 + view.m22*local.m21 + view.m23*local.m31;
		m22 = view.m21*local.m12 + view.m22*local.m22 + view.m23*local.m32;
		m23 = view.m21*local.m13 + view.m22*local.m23 + view.m23*local.m33;
		
		m31 = view.m31*local.m11 + view.m32*local.m21 + view.m33*local.m31;
		m32 = view.m31*local.m12 + view.m32*local.m22 + view.m33*local.m32;
		m33 = view.m31*local.m13 + view.m32*local.m23 + view.m33*local.m33;
	}
	
	public void multiplyView(Matrix3x3 view) {
		multm.multiply(view, this);
		copy(multm);
	}
	
	public void rotate(double theta) {
		/* equivalent to the following
		 * 
		 * tempm.m11 = cos;
		 * tempm.m12 = -sin;
		 * tempm.m21 = sin;
		 * tempm.m22 = cos;
		 * 
		 * multiply(tempm);
		 */
		
		double cos = Math.cos(theta);
		double sin = Math.sin(theta);
		
		double c1 = cos*m11 + m12*sin;
		double c2 = cos*m12 - m11*sin;
		m11 = c1;
		m12 = c2;
		
		c1 = cos*m21 + m22*sin;
		c2 = cos*m22 - m21*sin;
		m21 = c1;
		m22 = c2;
		
		c1 = cos*m31 + m32*sin;
		c2 = cos*m32 - m31*sin;
		m31 = c1;
		m32 = c2;
	}
	
	public void scale(double sx, double sy) {
		/* equivalent to the following
		 * 
		 * tempm.m11 = sx;
		 * tempm.m22 = sy;
		 * 
		 * multiply(target, tempm);
		 */
		
		m11 *= sx;
		m21 *= sx;
		m31 *= sx;
		
		m12 *= sy;
		m22 *= sy;
		m32 *= sy;
	}
	
	public void setToIdentity() {
		m11 = 1; m12 = 0; m13 = 0;
		m21 = 0; m22 = 1; m23 = 0;
		m31 = 0; m32 = 0; m33 = 1;
	}
	
	public void shearX(double value) {
		/* equivalent to the following
		 * 
		 * tempm.m12 = value;
		 * multiply(target, tempm);
		 */
		
		m12 += m11*value;
		m22 += m21*value;
		m32 += m31*value;
	}
	
	public void shearY(double value) {
		/* equivalent to the following
		 * 
		 * tempm.m21 = value;
		 * multiply(target, tempm);
		 */
		
		m11 += m12*value;
		m21 += m22*value;
		m31 += m32*value;
	}
	
	public double transformX(double x, double y, double w) {
		return x*m11 + y*m12 + w*m13;
	}
	
	public double transformY(double x, double y, double w) {
		return x*m21 + y*m22 + w*m23;
	}

	public void translate(double tx, double ty) {
		/* equivalent to the following
		 * 
		 * tempm.m13 = tx;
		 * tempm.m23 = ty;
		 * multiply(target, tempm);
		 */
		
		// adding translation vector in changed basis
		m13 += m11*tx + m12*ty;
		m23 += m21*tx + m22*ty;
		m33 += m31*tx + m32*ty;
	}
}
