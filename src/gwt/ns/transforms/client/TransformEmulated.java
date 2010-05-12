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
 * An implementation of {@link Transform}, using a Javascript-based 4x4 matrix.
 */
class TransformEmulated extends Transform {
	Matrix4x4 transform;
	
	TransformEmulated() {
		transform = new Matrix4x4();
	}
	
	@Override
	public double getM11() {
		return transform.m11;
	}

	@Override
	public double getM12() {
		return transform.m12;
	}

	@Override
	public double getM13() {
		return transform.m13;
	}

	@Override
	public double getM14() {
		return transform.m14;
	}

	@Override
	public double getM21() {
		return transform.m21;
	}

	@Override
	public double getM22() {
		return transform.m22;
	}

	@Override
	public double getM23() {
		return transform.m23;
	}

	@Override
	public double getM24() {
		return transform.m24;
	}

	@Override
	public double getM31() {
		return transform.m31;
	}

	@Override
	public double getM32() {
		return transform.m32;
	}

	@Override
	public double getM33() {
		return transform.m33;
	}

	@Override
	public double getM34() {
		return transform.m34;
	}

	@Override
	public double getM41() {
		return transform.m41;
	}

	@Override
	public double getM42() {
		return transform.m42;
	}

	@Override
	public double getM43() {
		return transform.m43;
	}

	@Override
	public double getM44() {
		return transform.m44;
	}

	@Override
	public void inverse(Transform dest) {
		transform.inverse(((TransformEmulated) dest).transform);
	}
	
	@Override
	public void inverseOrthonormalAffine(Transform dest) {
		transform.inverseOrthonormalAffine(((TransformEmulated) dest).transform);
	}

	@Override
	public void multiply(Transform local) {
		Matrix4x4 localMatrix = ((TransformEmulated) local).transform;
		
		transform.multiply(localMatrix);
	}

	@Override
	public void multiply(Transform view, Transform local) {
		Matrix4x4 viewMatrix = ((TransformEmulated) view).transform;
		Matrix4x4 localMatrix = ((TransformEmulated) local).transform;
		
		transform.multiply(viewMatrix, localMatrix);
	}

	@Override
	public void multiplyView(Transform view) {
		Matrix4x4 viewMatrix = ((TransformEmulated) view).transform;
		
		transform.multiplyView(viewMatrix);
	}

	@Override
	public void rotateX(double theta) {
		transform.rotateX(theta);
	}

	@Override
	public void rotateY(double theta) {
		transform.rotateY(theta);
	}

	@Override
	public void rotateZ(double theta) {
		transform.rotateZ(theta);
	}

	@Override
	public void scale(double sx, double sy) {
		transform.scale(sx, sy);
	}

	@Override
	public void scale(double sx, double sy, double sz) {
		transform.scale(sx, sy, sz);
	}

	@Override
	public void setM11(double value) {
		transform.m11 = value;
	}

	@Override
	public void setM12(double value) {
		transform.m12 = value;
	}

	@Override
	public void setM13(double value) {
		transform.m13 = value;
	}

	@Override
	public void setM14(double value) {
		transform.m14 = value;
	}

	@Override
	public void setM21(double value) {
		transform.m21 = value;
	}

	@Override
	public void setM22(double value) {
		transform.m22 = value;
	}

	@Override
	public void setM23(double value) {
		transform.m23 = value;
	}

	@Override
	public void setM24(double value) {
		transform.m24 = value;
	}

	@Override
	public void setM31(double value) {
		transform.m31 = value;
	}

	@Override
	public void setM32(double value) {
		transform.m32 = value;
	}

	@Override
	public void setM33(double value) {
		transform.m33 = value;
	}

	@Override
	public void setM34(double value) {
		transform.m34 = value;
	}

	@Override
	public void setM41(double value) {
		transform.m41 = value;
	}

	@Override
	public void setM42(double value) {
		transform.m42 = value;
	}

	@Override
	public void setM43(double value) {
		transform.m43 = value;
	}

	@Override
	public void setM44(double value) {
		transform.m44 = value;
	}

	@Override
	public void setToIdentity() {
		transform.setToIdentity();
	}

	@Override
	public void setTransform(Matrix4x4 src) {
		transform.copy(src);
	}
	
	@Override
	public void setTransform(Transform src) {
		setTransform(((TransformEmulated) src).transform);
	}

	@Override
	public void skewX(double theta) {
		transform.shearX(Math.tan(theta));
	}

	@Override
	public void skewY(double theta) {
		transform.shearY(Math.tan(theta));
	}

	@Override
	public double transformX(double x, double y, double z, double w) {
		return transform.transformX(x, y, z, w);
	}

	@Override
	public double transformY(double x, double y, double z, double w) {
		return transform.transformY(x, y, z, w);
	}

	@Override
	public double transformZ(double x, double y, double z, double w) {
		return transform.transformZ(x, y, z, w);
	}
	
	@Override
	public double transformW(double x, double y, double z, double w) {
		return transform.transformW(x, y, z, w);
	}
	
	@Override
	public void translate(double tx, double ty) {
		transform.translate(tx, ty);
	}

	@Override
	public void translate(double tx, double ty, double tz) {
		transform.translate(tx, ty, tz);
	}
}
