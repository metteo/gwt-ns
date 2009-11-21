package gwt.ns.transforms.client.impl;

/**
 * 4x4 matrix for representation of affine transformations
 * (in homogenous coordinates)
 * interfaces for 2d and 3d transformations, based on CSSMatrix
 * @see <a href="http://www.w3.org/TR/css3-2d-transforms/">W3C 2D Transforms Draft</a> 
 * @see <a href="http://www.w3.org/TR/css3-3d-transforms/">W3C 3D Transforms Draft</a>
 * 
 * most transformation methods are static to allow user to manage
 * memory usage (most methods on CSSMatrix return new matrices)
 * note that this, at least for now, precludes relying on e.g. safari's native
 * implementation, and thus hardware acceleration(?) on iphone
 *
 */
public class Matrix4x4 {
	// representation of matrix
	// first number is row number, second is column num
	public double m11, m21, m31, m41, m12, m22, m32, m42, m13, m23, m33, m43,
					m14, m24, m34, m44;
	
	// "scratch" matrices needed for multiplication and transformations
	// declared static since we are single threaded
	private static Matrix4x4 multm = new Matrix4x4();
	
	
	private static Matrix4x4 tempm = new Matrix4x4();
	
	/**
	 * Construct a new 4x4 matrix set to identity
	 */
	public Matrix4x4() {
		identity(this);
	}
	
	/**
	 * Multiply matrix a by b, store result in dest:
	 * dest = a*b
	 * 
	 * @param a left-hand matrix
	 * @param b right-hand matrix
	 * @param dest result of multiplication, can be a or b, which will be overwritten
	 */
	public static final void multiply(Matrix4x4 a, Matrix4x4 b, Matrix4x4 dest) {		
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
		
		// copy result to dest
		copy(multm, dest);
	}
	
	/**
	 * Rotates matrix target by angle theta in user coordinates
	 * 
	 * Note: due to the implicit viewport transform (+y points down on the
	 * screen), positive angles rotate clockwise.
	 * 
	 * @param angle The angle of rotation in degrees.
	 * @param target The matrix that is to be rotated
	 */
	public static final void rotate(Matrix4x4 target, double theta) {
		theta = Math.toRadians(theta);
		double cos = Math.cos(theta);
		double sin = Math.sin(theta);
		
		identity(tempm);
		tempm.m11 = cos;
		tempm.m12 = -sin;
		tempm.m21 = sin;
		tempm.m22 = cos;
		
		userMultiply(target, tempm);
	}
	/**
	 * Scales target matrix by the given vector in user coordinates
	 * 
	 * @param scaleX The x component in the vector.
	 * @param scaleY The y component in the vector.
	 * @return A new matrix that is the result of scaling this matrix.
	 */
	public static final void scale(Matrix4x4 target, double sx, double sy) {
		identity(tempm);
		tempm.m11 = sx;
		tempm.m22 = sy;
		
		userMultiply(target, tempm);
	}
	
	/**
	 * Translates this matrix by a given vector in user coordinates.
	 * 
	 * @param x The x component in the vector.
	 * @param y The y component in the vector.
	 */
	public static final void translate(Matrix4x4 target, double tx, double ty) {
		identity(tempm);
		tempm.m13 = tx;
		tempm.m23 = ty;
		
		userMultiply(target, tempm);
	}
	
	
	/**
	 * copies matrix data from src into dest
	 * 
	 * @param src the source matrix
	 * @param dest the destination matrix
	 */
	public static final void copy(Matrix4x4 src, Matrix4x4 dest) {
		dest.m11 = src.m11; dest.m12 = src.m12; dest.m13 = src.m13; dest.m14 = src.m14;
		dest.m21 = src.m21; dest.m22 = src.m22; dest.m23 = src.m23; dest.m24 = src.m24;
		dest.m31 = src.m31; dest.m32 = src.m32; dest.m33 = src.m33; dest.m34 = src.m34;
		dest.m41 = src.m41; dest.m42 = src.m42; dest.m43 = src.m43; dest.m44 = src.m44;
	}
	
	/**
	 * set an Matrix4x4 to the identity matrix
	 * 
	 * @param matrix the Matrix4x4 to alter
	 */
	public static final void identity(Matrix4x4 matrix) {
		matrix.m11 = 1; matrix.m12 = 0; matrix.m13 = 0; matrix.m14 = 0;
		matrix.m21 = 0; matrix.m22 = 1; matrix.m23 = 0; matrix.m24 = 0;
		matrix.m31 = 0; matrix.m32 = 0; matrix.m33 = 1; matrix.m34 = 0;
		matrix.m41 = 0; matrix.m42 = 0; matrix.m43 = 1; matrix.m44 = 0;
	}
	
	/**
	 * Applies a transform in user coordinates to the target matrix
	 * 
	 * @param target Matrix to apply the transform to, in view coordinates
	 * @param transform The transform to apply
	 */
	public static final void userMultiply(Matrix4x4 target, Matrix4x4 transform) {
		multiply(target, transform, target);
	}
	
	/**
	 * Applies a transform in view coordinates to the target matrix
	 * 
	 * @param target Matrix to apply the transform to, in view coordinates
	 * @param transform The transform to apply
	 */
	public static final void viewMultiply(Matrix4x4 target, Matrix4x4 transform) {
		multiply(transform, target, target);
	}
	
}
