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

package gwt.ns.transforms.client;

/*
 *	TODO:
	inverse (why? maybe wait until use case comes up for designing api)
	units?? ems, px, cm allowed in firefox at least...
	get copy? need to think about differed binding for this
	reset vs set to identity?
	push/pop()?
*/

public interface Transformable {

	/**
	 * Rotation in <em>local</em> (transformed) coordinates by 
	 * angle theta.<br><br>
	 * <strong>Note:</strong> due to definition of screen coordinates
	 * (with positive y pointing down), positive values of angle rotate
	 * <em>clockwise</em>.
	 * 
	 * @param theta The angle to rotate, in radians
	 */
	public void rotate(double theta);

	/**
	 * Rotation in <em>view</em> coordinates by angle theta.<br><br>
	 * <strong>Note:</strong> due to definition of screen coordinates
	 * (with positive y pointing down), positive values of theta rotate
	 * <em>clockwise</em>.
	 * 
	 * @param theta The angle to rotate in radians
	 */
	public void rotateView(double theta);

	/**
	 * Scale in <em>local</em> (transformed) coordinates by vector (sx, sy).
	 * 
	 * @param sx The scaling along the local x-axis
	 * @param sy The scaling along the local y-axis
	 */
	public void scale(double sx, double sy);

	/**
	 * Scale in <em>view</em> coordinates by vector (sx, sy).
	 * 
	 * @param sx The scaling along view x-axis
	 * @param sy The scaling along view y-axis
	 */
	public void scaleView(double sx, double sy);

	/**
	 * Reset this transformation to the identity transform.
	 */
	public void setToIdentity();
	// TODO: not wild about that method signature, but got it from
	// java.awt.geom.AffineTransform so good for now

	/**
	 * Set new values in the transformation matrix.
	 * The order is very specific, with each parameter specified first by
	 * row, then column. This is sometimes known as column-major ordering.
	 * 
	 * <pre>
	 * [ m11, m12, m13, m14 ]		[ 1st, 5th,  9th, 13th ]
	 * [ m21, m22, m23, m24 ]	->	[ 2nd, 6th, 10th, 14th ]
	 * [ m21, m12, m23, m24 ]		[ 3rd, 7th, 11th, 15th ]
	 * [ m21, m12, m23, m24 ]		[ 4th, 8th, 12th, 16th ]
	 * </pre>
	 * 
	 * @param t11-t44 a new value. t[row][column] represents the
	 * ([row],[column])th entry of the matrix
	 */
	public void setTransform(double t11, double t21, double t31, double t41,
			double t12, double t22, double t32, double t42, double t13,
			double t23, double t33, double t43, double t14, double t24,
			double t34, double t44);
	
	/**
	 * Set new values in the transformation matrix.
	 * 
	 * @param transfrom The new transform to copy into this one
	 */
	public void setTransform(Transform transfrom);
	
	/**
	 * Skews <em>local</em> (transformed) coordinates around the x-axis by 
	 * the given angle.
	 * 
	 * @param angle The skew angle.
	 */
	public void skewX(double angle);
	
	/**
	 * Skews <em>view</em> coordinates around the x-axis by the given angle.
	 * 
	 * @param angle The skew angle.
	 */
	public void skewXView(double angle);
	
	/**
	 * Skews <em>local</em> (transformed) coordinates around the y-axis by
	 * the given angle.
	 * 
	 * @param angle The skew angle.
	 */
	public void skewY(double angle);

	/**
	 * Skews <em>view</em> coordinates around the y-axis by the given angle.
	 * 
	 * @param angle The skew angle.
	 */
	public void skewYView(double angle);

	/**
	 * Apply a transformation to the current <em>local</em> coordinate system.
	 * This is the slowest way to multiply (though for implementation in pure
	 * java/js most of this should be inlined and the performance will be the
	 * same). if you can do a multiply in a subclass (or natively), do so.
	 * 
	 * @param transform the transformation to apply
	 */
	public void transform(Transform transform);

	/**
	 * Apply a transformation to the current <em>view</em> coordinate system.
	 * This is the slowest way to multiply. If you can do a multiply
	 * in a subclass (or natively), do so.
	 * 
	 * @param transform the transformation to apply
	 */
	public void transformView(Transform transform);

	/**
	 * Returns the x-component of the image of local-space point (x, y)
	 * under the current transform.
	 * 
	 * @param x The x coordinate of point to transform
	 * @param y The y coordinate of point to transform
	 * @return The x component of the transformed point
	 */
	public double transformX(double x, double y);
	
	/**
	 * Returns the y-component of the image of local-space point (x, y)
	 * under the current transform.
	 * 
	 * @param x The x coordinate of point to transform
	 * @param y The y coordinate of point to transform
	 * @return The y component of the transformed point
	 */
	public double transformY(double x, double y);

	/**
	 * Translation in <em>local</em> (transformed) coordinates by vector (tx, ty).
	 * 
	 * @param tx The translation along local x-axis
	 * @param ty The translation along local y-axis
	 */
	public void translate(double tx, double ty);

	/**
	 * Translation in <em>view</em> coordinates by vector (tx, ty).
	 * 
	 * @param tx The translation along view x-axis
	 * @param ty The translation along view y-axis
	 */
	public void translateView(double tx, double ty);
}