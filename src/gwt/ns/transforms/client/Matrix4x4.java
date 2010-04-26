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
 * 4x4 matrix for representation of affine transformations (in homogenous
 * coordinates). Interfaces for 2d and 3d transformations based on CSSMatrix.
 * 
 * <p>Note that, unlike CSSMatrix, Matrix4x4s are mutable. For non-destructive
 * concatenation of transforms, use the static
 * {@link #multiply(Matrix4x4, Matrix4x4, Matrix4x4)}.</p>
 * 
 * @see <a href="http://www.w3.org/TR/css3-2d-transforms/">W3C 2D Transforms Draft</a> 
 * @see <a href="http://www.w3.org/TR/css3-3d-transforms/">W3C 3D Transforms Draft</a>
 *
 */
public class Matrix4x4 {
	private static boolean clinit_guard;
	
	/**
	 * "Scratch" matrix needed for multiplication temporary. Declared static
	 * since we are single threaded.
	 */
	private static Matrix4x4 multm;
	
	/**
	 * Multiplies matrix a by b and stores the result in dest.
	 * dest is overwritten in process of multiplication, so should not itself
	 * be a or b. Note that this matrix is "column-major," so b will transform
	 * the image of a (i.e. b will transform the local coordinate system
	 * created by a).
	 * 
	 * <pre>dest = a*b</pre>
	 * 
	 * @param a left-hand matrix
	 * @param b right-hand matrix
	 * @param dest result of multiplication
	 */
	public static final void multiply(Matrix4x4 dest, Matrix4x4 a, Matrix4x4 b) {
		assert (!dest.equals(a) && !dest.equals(b)) : "destination matrix should not be one of the factors";
		
		// results stored in dest
		dest.m11 = a.m11*b.m11 + a.m12*b.m21 + a.m13*b.m31 + a.m14*b.m41;
		dest.m12 = a.m11*b.m12 + a.m12*b.m22 + a.m13*b.m32 + a.m14*b.m42;
		dest.m13 = a.m11*b.m13 + a.m12*b.m23 + a.m13*b.m33 + a.m14*b.m43;
		dest.m14 = a.m11*b.m14 + a.m12*b.m24 + a.m13*b.m34 + a.m14*b.m44;
		
		dest.m21 = a.m21*b.m11 + a.m22*b.m21 + a.m23*b.m31 + a.m24*b.m41;
		dest.m22 = a.m21*b.m12 + a.m22*b.m22 + a.m23*b.m32 + a.m24*b.m42;
		dest.m23 = a.m21*b.m13 + a.m22*b.m23 + a.m23*b.m33 + a.m24*b.m43;
		dest.m24 = a.m21*b.m14 + a.m22*b.m24 + a.m23*b.m34 + a.m24*b.m44;
		
		dest.m31 = a.m31*b.m11 + a.m32*b.m21 + a.m33*b.m31 + a.m34*b.m41;
		dest.m32 = a.m31*b.m12 + a.m32*b.m22 + a.m33*b.m32 + a.m34*b.m42;
		dest.m33 = a.m31*b.m13 + a.m32*b.m23 + a.m33*b.m33 + a.m34*b.m43;
		dest.m34 = a.m31*b.m14 + a.m32*b.m24 + a.m33*b.m34 + a.m34*b.m44;
		
		dest.m41 = a.m41*b.m11 + a.m42*b.m21 + a.m43*b.m31 + a.m44*b.m41;
		dest.m42 = a.m41*b.m12 + a.m42*b.m22 + a.m43*b.m32 + a.m44*b.m42;
		dest.m43 = a.m41*b.m13 + a.m42*b.m23 + a.m43*b.m33 + a.m44*b.m43;
		dest.m44 = a.m41*b.m14 + a.m42*b.m24 + a.m43*b.m34 + a.m44*b.m44;
	}
	
	/**
	 * Internal representation of a matrix entry.
	 * First number is row number, second is column number.
	 */
	public double m11, m21, m31, m41, m12, m22, m32, m42, m13, m23, m33,
					m43, m14, m24, m34, m44;
	
	/**
	 * Construct a new 4x4 matrix set to identity
	 */
	public Matrix4x4() {
		// hackery to have static temp matrix but avoid clinits
		if (!clinit_guard) {
			clinit_guard = true;
			multm = new Matrix4x4();
		}
		
		setToIdentity();
	}
	
	/**
	 * Applies a transform in <em>local</em> coordinates to the target matrix.
	 * 
	 * @param transform The transform to apply.
	 */
	public void multiply(Matrix4x4 transform) {
		multiply(multm, this, transform);
		set(multm);
	}
	
	/**
	 * Applies a transform in <em>view</em> coordinates to the target matrix.
	 * 
	 * @param transform The transform to apply
	 */
	public void multiplyView(Matrix4x4 transform) {
		multiply(multm, transform, this);
		set(multm);
	}
	
	/**
	 * Rotates matrix about x-axis by angle, in <em>local</em> coordinates.
	 * 
	 * @param theta The angle of rotation, in radians.
	 */
	public void rotateX(double theta) {
		/* equivalent to the following, but since tempm is mostly the identity,
		 * saves 3/4 of the multiplies and the copy.
		 * 
		 * tempm.m22 = cos;
		 * tempm.m23 = -sin;
		 * tempm.m32 = sin;
		 * tempm.m33 = cos;
		 * 
		 * multiply(tempm);
		 */
		
		double cos = Math.cos(theta);
		double sin = Math.sin(theta);
		
		double c2 = cos*m12 + m13*sin;
		double c3 = cos*m13 - m12*sin;
		m12 = c2;
		m13 = c3;
		
		c2 = cos*m22 + m23*sin;
		c3 = cos*m23 - m22*sin;
		m22 = c2;
		m23 = c3;
		
		c2 = cos*m32 + m33*sin;
		c3 = cos*m33 - m32*sin;
		m32 = c2;
		m33 = c3;
		
		c2 = cos*m42 + m43*sin;
		c3 = cos*m43 - m42*sin;
		m42 = c2;
		m43 = c3;
	}
	
	/**
	 * Rotates matrix about y-axis by angle, in <em>local</em> coordinates.
	 * 
	 * @param theta The angle of rotation, in radians
	 */
	public void rotateY(double theta) {
		/* equivalent to the following, but since tempm is mostly the identity,
		 * saves 3/4 of the multiplies and the copy.
		 * 
		 * tempm.m11 = cos;
		 * tempm.m13 = sin;
		 * tempm.m31 = -sin;
		 * tempm.m33 = cos;
		 * 
		 * multiply(tempm);
		 */
		
		double cos = Math.cos(theta);
		double sin = Math.sin(theta);
		
		double c1 = cos*m11 - m13*sin;
		double c3 = cos*m13 + m11*sin;
		m11 = c1;
		m13 = c3;
		
		c1 = cos*m21 - m23*sin;
		c3 = cos*m23 + m21*sin;
		m21 = c1;
		m23 = c3;
		
		c1 = cos*m31 - m33*sin;
		c3 = cos*m33 + m31*sin;
		m31 = c1;
		m33 = c3;
		
		c1 = cos*m41 - m43*sin;
		c3 = cos*m43 + m41*sin;
		m41 = c1;
		m43 = c3;
	}
	
	/**
	 * Rotates matrix about z-axis by angle, in <em>local</em> coordinates.
	 * 
	 * @param theta The angle of rotation, in radians
	 */
	public void rotateZ(double theta) {
		/* equivalent to the following, but since tempm is mostly the identity,
		 * saves 3/4 of the multiplies and the copy.
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
		
		c1 = cos*m31 + m32*sin;
		c2 = cos*m32 - m31*sin;
		m41 = c1;
		m42 = c2;
	}
	
	/**
	 * Scales the matrix by the given vector in <em>local</em> coordinates.
	 * 
	 * @param sx The x coordinate of the scale vector
	 * @param sy The y coordinate of the scale vector
	 * @param sz The z coordinate of the scale vector
	 */
	public void scale(double sx, double sy, double sz) {
		/* equivalent to the following, but since tempm is mostly the identity,
		 * saves 13/16 of the multiplies and the copy.
		 * 
		 * tempm.m11 = sx;
		 * tempm.m22 = sy;
		 * tempm.m33 = sz;
		 * 
		 * multiply(tempm);
		 */
		
		// just scaling the basis vectors (columns)
		m11 *= sx;
		m21 *= sx;
		m31 *= sx;
		m41 *= sx;
		
		m12 *= sy;
		m22 *= sy;
		m32 *= sy;
		m42 *= sy;
		
		m13 *= sz;
		m23 *= sz;
		m33 *= sz;
		m43 *= sz;
	}
	
	/**
	 * Sets this matrix to the specified transform.
	 * 
	 * @param transform The transform to be copied.
	 */
	public void set(Matrix4x4 transform) {
		m11 = transform.m11; m12 = transform.m12; m13 = transform.m13; m14 = transform.m14;
		m21 = transform.m21; m22 = transform.m22; m23 = transform.m23; m24 = transform.m24;
		m31 = transform.m31; m32 = transform.m32; m33 = transform.m33; m34 = transform.m34;
		m41 = transform.m41; m42 = transform.m42; m43 = transform.m43; m44 = transform.m44;
	}
	
	/**
	 * Set this matrix to the identity.
	 */
	public void setToIdentity() {
		m11 = 1; m12 = 0; m13 = 0; m14 = 0;
		m21 = 0; m22 = 1; m23 = 0; m24 = 0;
		m31 = 0; m32 = 0; m33 = 1; m34 = 0;
		m41 = 0; m42 = 0; m43 = 0; m44 = 1;
	}
	
	/**
	 * Skews the matrix around the x-axis by the given angle, in <em>local</em>
	 * coordinates.
	 * 
	 * @param theta The angle of the skew, in radians.
	 */
	public void skewX(double theta) {
		/* equivalent to the following, but since tempm is mostly the identity,
		 * saves 15/16 of the multiplies and the copy.
		 * 
		 * tempm.m12 = Math.tan(theta);
		 * multiply(tempm);
		 */
		
		double tan = Math.tan(theta);
		m12 += m11*tan;
		m22 += m21*tan;
		m32 += m31*tan;
		m42 += m41*tan;
	}
	
	/**
	 * Skews the matrix around the y-axis by the given angle, in <em>local</em>
	 * coordinates.
	 * 
	 * @param theta The angle of the skew, in radians
	 */
	public void skewY(double theta) {
		/* equivalent to the following, but since tempm is mostly the identity,
		 * saves 15/16 of the multiplies and the copy.
		 * 
		 * tempm.m21 = Math.tan(theta);
		 * multiply(tempm);
		 */
		
		double tan = Math.tan(theta);
		m11 += m12*tan;
		m21 += m22*tan;
		m31 += m32*tan;
		m41 += m42*tan;
	}
	
	/**
	 * Returns the x-component of the image of point (x, y, z, w) under the
	 * current transform. A w-value of 1 is generally used for points, 0 for
	 * vectors.
	 * 
	 * @param x The x coordinate of point to transform
	 * @param y The y coordinate of point to transform
	 * @param z The z coordinate of point to transform
	 * @param w The w coordinate of point to transform
	 * @return The x coordinate of the transformed point
	 */
	public double transformX(double x, double y, double z, double w) {
		return x*m11 + y*m12 + z*m13 + w*m14;
	}
	
	/**
	 * Returns the y-component of the image of local-space point (x, y, z, w)
	 * under the current transform. A w value of 1 is generally used for
	 * points, 0 for vectors.
	 * 
	 * @param x The x coordinate of point to transform
	 * @param y The y coordinate of point to transform
	 * @param z The z coordinate of point to transform
	 * @param w The w coordinate of point to transform
	 * @return The y coordinate of the transformed point
	 */
	public double transformY(double x, double y, double z, double w) {
		return x*m21 + y*m22 + z*m23 + w*m24;
	}
	
	/**
	 * Returns the z-component of the image of local-space point (x, y, z, w)
	 * under the current transform. A w value of 1 is generally used for
	 * points, 0 for vectors.
	 * 
	 * @param x The x coordinate of point to transform
	 * @param y The y coordinate of point to transform
	 * @param z The z coordinate of point to transform
	 * @param w The w coordinate of point to transform
	 * @return The z coordinate of the transformed point
	 */
	public double transformZ(double x, double y, double z, double w) {
		return x*m31 + y*m32 + z*m33 + w*m34;
	}
	
	/**
	 * Translates the matrix by a given vector, in <em>local</em> coordinates.
	 * 
	 * @param tx The x coordinate of the translation vector
	 * @param ty The y coordinate of the translation vector
	 * @param tz The z coordinate of the translation vector
	 */
	public void translate(double tx, double ty, double tz) {
		/* equivalent to the following, but since tempm is mostly the identity,
		 * saves 13/16 of the multiplies and the copy.
		 * 
		 * tempm.m14 = tx;
		 * tempm.m24 = ty;
		 * tempm.m34 = tz;
		 * multiply(tempm);
		 */
		
		// adding translation vector in changed basis
		m14 += m11*tx + m12*ty + m13*tz;
		m24 += m21*tx + m22*ty + m23*tz;
		m34 += m31*tx + m32*ty + m33*tz;
		m44 += m41*tx + m42*ty + m43*tz;
	}
}
