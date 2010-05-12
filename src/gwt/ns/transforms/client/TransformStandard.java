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
 * An implementation of {@link Transform}, using a native CSSMatrix for
 * calculations.
 */
class TransformStandard extends Transform {
	CssMatrix transform;
	
	TransformStandard() {
		setToIdentity();
	}
	
	@Override
	public double getM11() {
		return transform.getM11();
	}
	
	@Override
	public double getM12() {
		return transform.getM12();
	}
	
	@Override
	public double getM13() {
		return transform.getM13();
	}
	
	@Override
	public double getM14() {
		return transform.getM14();
	}

	@Override
	public double getM21() {
		return transform.getM21();
	}

	@Override
	public double getM22() {
		return transform.getM22();
	}
	
	@Override
	public double getM23() {
		return transform.getM23();
	}
	
	@Override
	public double getM24() {
		return transform.getM24();
	}
	
	@Override
	public double getM31() {
		return transform.getM31();
	}
	
	@Override
	public double getM32() {
		return transform.getM32();
	}
	
	@Override
	public double getM33() {
		return transform.getM33();
	}
	
	@Override
	public double getM34() {
		return transform.getM34();
	}
	
	@Override
	public double getM41() {
		return transform.getM41();
	}
	
	@Override
	public double getM42() {
		return transform.getM42();
	}
	
	@Override
	public double getM43() {
		return transform.getM43();
	}
	
	@Override
	public double getM44() {
		return transform.getM44();
	}
	
	@Override
	public void inverse(Transform dest) {
		((TransformStandard) dest).transform = transform.inverse();
	}
	
	@Override
	public void inverseOrthonormalAffine(Transform dest) {
		// TODO: investigate performance
		// such a simple method probably faster than c++ impl
		((TransformStandard) dest).transform = transform.inverse();
	}
	
	@Override
	public void multiply(Transform local) {
		CssMatrix localMatrix = ((TransformStandard) local).transform;
		
		transform = transform.multiply(localMatrix);
	}
	
	@Override
	public void multiply(Transform view, Transform local) {
		CssMatrix viewMatrix = ((TransformStandard) view).transform;
		CssMatrix localMatrix = ((TransformStandard) local).transform;
		
		transform = viewMatrix.multiply(localMatrix);
	}
	
	@Override
	public void multiplyView(Transform view) {
		CssMatrix viewMatrix = ((TransformStandard) view).transform;
		
		transform = viewMatrix.multiply(transform);
	}
	
	@Override
	public void rotate(double theta) {
		transform = transform.rotate(theta * (180 / Math.PI));
	}
	
	@Override
	public void rotateX(double theta) {
		transform = transform.rotateAxisAngle(1, 0, 0, theta * (180 / Math.PI));
	}

	@Override
	public void rotateY(double theta) {
		transform = transform.rotateAxisAngle(0, 1, 0, theta * (180 / Math.PI));
	}

	@Override
	public void rotateZ(double theta) {
		transform = transform.rotateAxisAngle(0, 0, 1, theta * (180 / Math.PI));
	}
	
	@Override
	public void scale(double sx, double sy) {
		transform = transform.scale(sx, sy);
	}

	@Override
	public void scale(double sx, double sy, double sz) {
		transform = transform.scale(sx, sy, sz);
	}

	@Override
	public void setM11(double value) {
		transform.setM11(value);
	}
	
	@Override
	public void setM12(double value) {
		transform.setM12(value);
	}

	@Override
	public void setM13(double value) {
		transform.setM13(value);
	}

	@Override
	public void setM14(double value) {
		transform.setM14(value);
	}

	@Override
	public void setM21(double value) {
		transform.setM21(value);
	}

	@Override
	public void setM22(double value) {
		transform.setM22(value);
	}

	@Override
	public void setM23(double value) {
		transform.setM23(value);
	}

	@Override
	public void setM24(double value) {
		transform.setM24(value);
	}

	@Override
	public void setM31(double value) {
		transform.setM31(value);
	}

	@Override
	public void setM32(double value) {
		transform.setM32(value);
	}

	@Override
	public void setM33(double value) {
		transform.setM33(value);
	}

	@Override
	public void setM34(double value) {
		transform.setM34(value);
	}

	@Override
	public void setM41(double value) {
		transform.setM41(value);
	}

	@Override
	public void setM42(double value) {
		transform.setM42(value);
	}

	@Override
	public void setM43(double value) {
		transform.setM43(value);
	}

	@Override
	public void setM44(double value) {
		transform.setM44(value);
	}

	@Override
	public void setToIdentity() {
		transform = createNewMatrix();
	}

	@Override
	public void setTransform(Transform src) {
		CssMatrix toCopy = ((TransformStandard) src).transform;
		transform = toCopy.translate(0, 0);
	}
	
	@Override
	public void setTransform(Matrix4x4 src) {
		// nothing elegant about this
		setM11(src.m11);
		setM12(src.m12);
		setM13(src.m13);
		setM14(src.m14);
		
		setM21(src.m21);
		setM22(src.m22);
		setM23(src.m23);
		setM24(src.m24);
		
		setM31(src.m31);
		setM32(src.m32);
		setM33(src.m33);
		setM34(src.m34);
		
		setM41(src.m41);
		setM42(src.m42);
		setM43(src.m43);
		setM44(src.m44);
	}

	@Override
	public void skewX(double theta) {
		transform = transform.skewX(theta * (180 / Math.PI));
	}

	@Override
	public void skewY(double theta) {
		transform = transform.skewY(theta * (180 / Math.PI));
	}

	@Override
	public String toCss2dTransformString() {
		return transform.toCssString();
	}

	@Override
	public String toCss3dTransformString() {
		return transform.toCssString();
	}
	
	@Override
	public double transformX(double x, double y, double z, double w) {
		// I don't think there's any other way to do this
		return x*getM11() + y*getM12() + z*getM13() + w*getM14();
	}

	@Override
	public double transformY(double x, double y, double z, double w) {
		return x*getM21() + y*getM22() + z*getM23() + w*getM24();
	}

	@Override
	public double transformZ(double x, double y, double z, double w) {
		return x*getM31() + y*getM32() + z*getM33() + w*getM34();
	}

	@Override
	public double transformW(double x, double y, double z, double w) {
		return x*getM41() + y*getM42() + z*getM43() + w*getM44();
	}
	
	@Override
	public void translate(double tx, double ty) {
		transform = transform.translate(tx, ty);
	}

	@Override
	public void translate(double tx, double ty, double tz) {
		transform = transform.translate(tx, ty, tz);
	}
	
	/**
	 * @return A new CssMatrix-like object
	 */
	CssMatrix createNewMatrix() {
		return CssMatrix.create();
	}
}
