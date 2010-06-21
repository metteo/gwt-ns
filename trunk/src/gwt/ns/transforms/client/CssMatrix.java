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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A JavaScript overlay for the CSSMatrix Javascript object. This interface is
 * currently unsupported by any browser, but this overlay can be used for any
 * experimental vendor implementations. The object must be created in native
 * code, e.g.
 * <pre>
 * final native CssMatrix createWebKitCssMatrix() /*-{
 *   return new WebKitCSSMatrix();
 * }-* /;</pre>
 * 
 * @see <a href="http://www.w3.org/TR/css3-2d-transforms/#cssmatrix-interface">Current working draft of CSS 2D Transforms Module Level 3</a>
 */
public class CssMatrix extends JavaScriptObject {
	protected CssMatrix() { }
	
	/**
	 * Construct a new matrix, set to identity. Note that no platform yet
	 * supports native CSSMatrices.
	 * 
	 * @return A new CssMatrix, set to the identity.
	 */
	public static final native CssMatrix create() /*-{
		return new CSSMatrix();
	}-*/;
	
	/**
	 * Returns the inverse of this matrix. This matrix is not modified.
	 * 
	 * @return A new matrix that is the inverse of this matrix.
	 * @throws JavaScriptException thrown when the matrix can not be inverted
	 */
	public final native CssMatrix inverse() /*-{
		return this.inverse();
	}-*/;
	
	/**
	 * Returns the result of multiplying this matrix by secondMatrix.
	 * secondMatrix is on the right of the multiplication.
	 * This matrix is not modified by this method.
	 * 
	 * @param secondMatrix The matrix to multiply.
	 * @return A matrix that is the product of this matrix and secondMatrix.
	 */
	public final native CssMatrix multiply(CssMatrix secondMatrix) /*-{
		return this.multiply(secondMatrix);
	}-*/;
	
	/**
	 * Returns a copy of this matrix rotated by angle in <em>local</em>
	 * coordinates. This matrix is not modified by this method.<br><br>
	 * 
	 * <strong>Note:</strong> due to the implicit viewport transform (+y points
	 * down on the screen), positive angles rotate clockwise.
	 * 
	 * @param angle The angle of rotation, in degrees
	 * @return A rotated matrix
	 */
	public final native CssMatrix rotate(double angle) /*-{
		return this.rotate(angle);
	}-*/;
	
	/**
	 * Returns a copy of this matrix which is this matrix post multiplied by
	 * each of 3 rotation matrices about the major axes, first X, then Y, then
	 * Z. All rotation values are in <em>degrees</em>. This matrix is not
	 * modified.
	 * 
	 * <p><strong>Note:</strong> the implicit viewport transform (+y points
	 * down on the screen), changes rotation directions accordingly.</p>
	 * 
	 * @param rotX The angle to rotate about the x axis
	 * @param rotY The angle to rotate about the y axis
	 * @param rotZ The angle to rotate about the z axis
	 * @return A rotated matrix
	 */
	public final native CssMatrix rotate(double rotX, double rotY, double rotZ) /*-{
		return this.rotate(rotX, rotY, rotZ);
	}-*/;
	
	/**
	 * Returns a new matrix which is this matrix post multiplied by a rotation
	 * matrix with the given axis and angle. The right-hand rule is used to
	 * determine the direction of rotation. All rotation values are in degrees.
	 * This matrix is not modified.
	 * 
	 * @param x The x-component of the axis vector
	 * @param y The y-component of the axis vector
	 * @param z The z-component of the axis vector
	 * @param angle The angle of rotation about the axis vector, in degrees
	 * @return A rotated matrix
	 */
	public final native CssMatrix rotateAxisAngle(double x, double y, double z, double angle) /*-{
		return this.rotateAxisAngle(x, y, z, angle);
	}-*/;
	
	/**
	 * Returns the result of scaling this matrix by the given vector in
	 * <em>local</em> coordinates. 
	 * This matrix is not modified by this method.
	 * 
	 * @param scaleX The x-component of the scaling vector.
	 * @param scaleY The y-component of the scaling vector.
	 * @return A new matrix that is the result of scaling this matrix.
	 */
	public final native CssMatrix scale(double scaleX, double scaleY) /*-{
		return this.scale(scaleX, scaleY);
	}-*/;
	
	/**
	 * Returns the result of scaling this matrix by the given vector in
	 * <em>local</em> coordinates. This matrix is not modified by this method.
	 * 
	 * @param scaleX The x-component of the scaling vector.
	 * @param scaleY The y-component of the scaling vector.
	 * @param scaleZ The z-component of the scaling vector.
	 * @return A new matrix that is the result of scaling this matrix.
	 */
	public final native CssMatrix scale(double scaleX, double scaleY, double scaleZ) /*-{
		return this.scale(scaleX, scaleY, scaleZ);
	}-*/;
	
	/**
	 * Returns the result of skewing this matrix around the x-axis by the
	 * given angle, in local coordinates.
	 * This matrix is not modified by this method.
	 * 
	 * <p>Note that this method is not yet widely implemented.</p>
	 * 
	 * @param angle The angle of the skew, in degrees
	 * @return A new matrix that is the result of skewing this matrix.
	 */
	public final native CssMatrix skewX(double angle) /*-{
		return this.skewX(angle);
	}-*/;
	
	/**
	 * Returns the result of skewing this matrix around the y-axis by the
	 * given angle, in local coordinates.
	 * This matrix is not modified by this method.
	 * 
	 * <p>Note that this method is not yet widely implemented.</p>
	 * 
	 * @param angle The angle of the skew, in degrees
	 * @return A new matrix that is the result of skewing this matrix.
	 */
	public final native CssMatrix skewY(double angle) /*-{
		return this.skewY(angle);
	}-*/;
	
	public final native String toCssString() /*-{
		return this.toString();
	}-*/;
	
	/**
	 * Returns the result of translating this matrix by a given vector, in
	 * local coordinates. This matrix is not modified by this method.
	 * 
	 * @param x The x-component in the vector.
	 * @param y The y-component in the vector.
	 * @return A new matrix that is the result of translating this matrix.
	 */
	public final native CssMatrix translate(double x, double y) /*-{
		return this.translate(x, y);
	}-*/;
	
	/**
	 * Returns the result of translating this matrix by a given vector, in
	 * local coordinates. This matrix is not modified by this method.
	 * 
	 * @param x The x-component of the translation vector.
	 * @param y The y-component of the translation vector.
	 * @param z The z-component of the translation vector.
	 * @return A new matrix that is the result of translating this matrix.
	 */
	public final native CssMatrix translate(double x, double y, double z) /*-{
		return this.translate(x, y, z);
	}-*/;
	
	/**
	 * Replaces the existing matrix with one computed from parsing the passed
	 * string as though it had been assigned to the transform property in a
	 * CSS style rule. 
	 * 
	 * @param matString The string to parse. Can be returned by window.getComputedStyle(element).transform().
	 * @throws JavaScriptException thrown when the provided string can not be parsed into a CSSMatrix. 
	 */
	public final native void setMatrixValue(String matString) /*-{
		return this.setMatrixValue(matString);
	}-*/;
	
	public final native double getM11() /*-{ return this.m11; }-*/;
	public final native void setM11(double m11) /*-{ this.m11 = m11; }-*/;

	public final native double getM21() /*-{ return this.m21; }-*/;
	public final native void setM21(double m21) /*-{ this.m21 = m21; }-*/;

	public final native double getM31() /*-{ return this.m31; }-*/;
	public final native void setM31(double m31) /*-{ this.m31 = m31; }-*/;

	public final native double getM41() /*-{ return this.m41; }-*/;
	public final native void setM41(double m41) /*-{ this.m41 = m41; }-*/;

	public final native double getM12() /*-{ return this.m12; }-*/;
	public final native void setM12(double m12) /*-{ this.m12 = m12; }-*/;

	public final native double getM22() /*-{ return this.m22; }-*/;
	public final native void setM22(double m22) /*-{ this.m22 = m22; }-*/;

	public final native double getM32() /*-{ return this.m32; }-*/;
	public final native void setM32(double m32) /*-{ this.m32 = m32; }-*/;

	public final native double getM42() /*-{ return this.m42; }-*/;
	public final native void setM42(double m42) /*-{ this.m42 = m42; }-*/;

	public final native double getM13() /*-{ return this.m13; }-*/;
	public final native void setM13(double m13) /*-{ this.m13 = m13; }-*/;

	public final native double getM23() /*-{ return this.m23; }-*/;
	public final native void setM23(double m23) /*-{ this.m23 = m23; }-*/;

	public final native double getM33() /*-{ return this.m33; }-*/;
	public final native void setM33(double m33) /*-{ this.m33 = m33; }-*/;

	public final native double getM43() /*-{ return this.m43; }-*/;
	public final native void setM43(double m43) /*-{ this.m43 = m43; }-*/;

	public final native double getM14() /*-{ return this.m14; }-*/;
	public final native void setM14(double m14) /*-{ this.m14 = m14; }-*/;

	public final native double getM24() /*-{ return this.m24; }-*/;
	public final native void setM24(double m24) /*-{ this.m24 = m24; }-*/;

	public final native double getM34() /*-{ return this.m34; }-*/;
	public final native void setM34(double m34) /*-{ this.m34 = m34; }-*/;

	public final native double getM44() /*-{ return this.m44; }-*/;
	public final native void setM44(double m44) /*-{ this.m44 = m44; }-*/;
}
