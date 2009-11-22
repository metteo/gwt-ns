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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Overlay for Webkit's WebKitCSSMatrix object
 * allows use of native browser functionality where available (and
 * possibly hardware acceleration on iphone, etc)
 * This overlay will be very close to the future standardized CSSMatrix.
 * 
 * Note that most methods return a new matrix and GC can be treacherous.
 * 
 * Note also that this code uses row vectors for points, which need to be
 * multiplied on the left. That also means that you must premultiply to make a
 * transform in local coordinates, postmultiply to make change in screen.
 * You will also find entries at the transpose of the positions found in many
 * graphics texts. e.g. translation in x is found in m41, not m14
 * 
 * This does not seem to be what is implied in the W3C drafts, which refer to
 * the svg spec (see <http://www.w3.org/TR/SVG/coords.html#NestedTransformations>here</a>),
 * but is internally consistent. Should only be an issue if you construct a
 * matrix by setting individual matrix entries.
 * 
 * @see <a href='http://www.w3.org/TR/css3-2d-transforms/#cssmatrix-interface>W3C draft specification of CSSMatrix</a>
 * @see <a href='http://svn.webkit.org/repository/webkit/trunk/WebCore/css/WebKitCSSMatrix.idl'>WebKitCSSMatrix idl</a>
 * @see <a href='http://developer.apple.com/safari/library/documentation/AppleApplications/Reference/SafariJSRef/WebKitCSSMatrix/WebKitCSSMatrix.html'>Safari documentation</a>
 */
public class WebKitCssMatrix extends JavaScriptObject {
	protected WebKitCssMatrix() { }
	
	/**
	 * Returns a new matrix, set to identity
	 * 
	 * @return A new WebKitCssMatrix set to the identity
	 */
	public static final native WebKitCssMatrix newInstance() /*-{
		return new WebKitCSSMatrix();
	}-*/;
	
	/**
	 * Return a copy of this matrix.
	 * This matrix is not modified.
	 * 
	 * @return a copy of the current matrix.
	 */
	public final WebKitCssMatrix getCopy() {
		// not a great method, but should be fast
		return translate(0, 0);
	}
	
	/**
	 * Returns the inverse of this matrix. This matrix is not modified.
	 * 
	 * @return A new matrix that is the inverse of this matrix.
	 * @throws JavaScriptException thrown when the matrix can not be inverted
	 */
	public final native WebKitCssMatrix inverse() /*-{
		return this.inverse();
	}-*/;
	
	/**
	 * Returns the result of multiplying this matrix by secondMatrix.
	 * secondMatrix is on the right of the multiplication.
	 * This matrix is not modified by this method.
	 * 
	 * @param secondMatrix The matrix to multiply.
	 * @return A matrix that is the product of this matrix and secondMatrix.
	 * @throws JavaScriptException presumably when multiplication is not possible, safari docs not explicit
	 */
	public final native WebKitCssMatrix multiply(WebKitCssMatrix secondMatrix) /*-{
		return this.multiply(secondMatrix);
	}-*/;
	
	/**
	 * Applies a transform in view coordinates to this matrix and returns
	 * the resulting matrix. This matrix is not modified by this method.
	 * 
	 * @param secondMatrix The transform to apply
	 * @return the transformed matrix
	 */
	public final WebKitCssMatrix viewMultiply(WebKitCssMatrix secondMatrix) {
		return multiply(secondMatrix);
	}
	
	/**
	 * Applies a transform in local coordinates to this matrix and returns
	 * the resulting matrix. This matrix is not modified by this method.
	 * 
	 * @param secondMatrix The transform to apply
	 * @return the transformed matrix
	 */
	public final WebKitCssMatrix localMultiply(WebKitCssMatrix secondMatrix) {
		return secondMatrix.multiply(this);
	}
	
	/**
	 * The rotate method returns a new matrix which is this matrix post
	 * multiplied by a rotation matrix. The rotation value is in degrees.
	 * This matrix is not modified.
	 * 
	 * Note: due to the implicit viewport transform (+y points down on the
	 * screen), positive angles rotate clockwise.
	 * 
	 * @param angle The angle of rotation in degrees.
	 * @return A matrix that is a rotation of this matrix by angle degrees
	 */
	public final native WebKitCssMatrix rotate(double angle) /*-{
		return this.rotate(angle);
	}-*/;
	
	/**
	 * Returns the result of scaling this matrix by the given vector
	 * This matrix is not modified by this method.
	 * 
	 * @param scaleX The x component in the vector.
	 * @param scaleY The y component in the vector.
	 * @return A new matrix that is the result of scaling this matrix.
	 */
	public final native WebKitCssMatrix scale(double scaleX, double scaleY) /*-{
		return this.scale(scaleX, scaleY);
	}-*/;
	
	
	/**
	 * Replaces existing matrix with identity matrix
	 */
	public final native void setToIdentity()  /*-{
		// ugly, but the only other option seems to be
		// setMatrixValue('translate(0)'); or the like
		this.m11 = this.m22 = this.m33 = this.m44 = 1;
		this.m21 = this.m31 = this.m41 = 0;
		this.m12 = this.m32 = this.m42 = 0;
		this.m13 = this.m23 = this.m43 = 0;
		this.m14 = this.m24 = this.m34 = 0;
	}-*/;
	
	/**
	 * Returns the result of translating this matrix by a given vector.
	 * This matrix is not modified by this method.
	 * 
	 * @param x The x component in the vector.
	 * @param y The y component in the vector.
	 * @return A new matrix that is the result of translating this matrix.
	 */
	public final native WebKitCssMatrix translate(double x, double y) /*-{
		return this.translate(x, y);
	}-*/;
	
	/**
	 * Replaces the existing matrix with one computed from parsing the passed
	 * string as though it had been assigned to the transform property in a
	 * CSS style rule. 
	 * 
	 * @param matString The string to parse. Can be returned by window.getComputedStyle(element).webkitTransform().
	 * @throws JavaScriptException thrown when the provided string can not be parsed into a CSSMatrix. 
	 */
	public final native void setMatrixValue(String matString) /*-{
		// TODO: check that exception is actually thrown, per W3C draft
		// safari docs don't mention exception
		return this.setMatrixValue(matString);
	}-*/;
	
	// not ideal, but will have to do. gwt should inline everything
	// access elements of the matrix
	// note that WebKitCSSMatrix is in row-major ordering
	public final native double m11() /*-{ return this.m11; }-*/;
	public final native void setM11(double m11) /*-{ this.m11 = m11; }-*/;

	public final native double m21() /*-{ return this.m21; }-*/;
	public final native void setM21(double m21) /*-{ this.m21 = m21; }-*/;

	public final native double m31() /*-{ return this.m31; }-*/;
	public final native void setM31(double m31) /*-{ this.m31 = m31; }-*/;

	public final native double m41() /*-{ return this.m41; }-*/;
	public final native void setM41(double m41) /*-{ this.m41 = m41; }-*/;

	public final native double m12() /*-{ return this.m12; }-*/;
	public final native void setM12(double m12) /*-{ this.m12 = m12; }-*/;

	public final native double m22() /*-{ return this.m22; }-*/;
	public final native void setM22(double m22) /*-{ this.m22 = m22; }-*/;

	public final native double m32() /*-{ return this.m32; }-*/;
	public final native void setM32(double m32) /*-{ this.m32 = m32; }-*/;

	public final native double m42() /*-{ return this.m42; }-*/;
	public final native void setM42(double m42) /*-{ this.m42 = m42; }-*/;

	public final native double m13() /*-{ return this.m13; }-*/;
	public final native void setM13(double m13) /*-{ this.m13 = m13; }-*/;

	public final native double m23() /*-{ return this.m23; }-*/;
	public final native void setM23(double m23) /*-{ this.m23 = m23; }-*/;

	public final native double m33() /*-{ return this.m33; }-*/;
	public final native void setM33(double m33) /*-{ this.m33 = m33; }-*/;

	public final native double m43() /*-{ return this.m43; }-*/;
	public final native void setM43(double m43) /*-{ this.m43 = m43; }-*/;

	public final native double m14() /*-{ return this.m14; }-*/;
	public final native void setM14(double m14) /*-{ this.m14 = m14; }-*/;

	public final native double m24() /*-{ return this.m24; }-*/;
	public final native void setM24(double m24) /*-{ this.m24 = m24; }-*/;

	public final native double m34() /*-{ return this.m34; }-*/;
	public final native void setM34(double m34) /*-{ this.m34 = m34; }-*/;

	public final native double m44() /*-{ return this.m44; }-*/;
	public final native void setM44(double m44) /*-{ this.m44 = m44; }-*/;
}