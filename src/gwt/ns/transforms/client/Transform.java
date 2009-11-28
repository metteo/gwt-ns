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

/**
 * TODO: fix this up.<br>
 * this is the abstract base class for Transform objects which internally use a matrix
 * Each entry in the 4x4 matrix is represented by m<row><column>.
 * For instance m12 represents the value in the 2nd column of the first row.
 * 
 * Note that this representation uses column vectors, meaning vectors are
 * transformed by multiplying them to the right of a transformation matrix.
 * This matches usual representation in computer graphics (see openGL).
 * 
 * The translation vector can be found in column 4 (m14, m24, and m34).
 * <pre>
 * [ m11, m12, m13, m14 ]
 * [ m21, m22, m23, m24 ]
 * [ m21, m12, m23, m24 ]
 * [ m21, m12, m23, m24 ]
 * </pre>
 */
public abstract class Transform implements Transformable {
	
	@Override
	public void setTransform(double t11, double t21, double t31, double t41, double t12,
					double t22, double t32, double t42, double t13, double t23,
					double t33, double t43, double t14, double t24, double t34,
					double t44) {
		setM11(t11);
		setM12(t12);
		setM13(t13);
		setM14(t14);
		setM21(t21);
		setM22(t22);
		setM23(t23);
		setM24(t24);
		setM31(t31);
		setM32(t32);
		setM33(t33);
		setM34(t34);
		setM41(t41);
		setM42(t42);
		setM43(t43);
		setM44(t44);
	}
	
	@Override
	public void setTransform(Transform t) {
		setTransform(t.m11(), t.m21(), t.m31(), t.m41(), t.m12(), t.m22(),
				t.m32(), t.m42(), t.m13(), t.m23(), t.m33(), t.m43(), t.m14(),
				t.m24(), t.m34(), t.m44());
	}
	

	@Override
	public void transform(Transform transform) {
		double t11 = transform.m11()*m11() + transform.m12()*m21() + transform.m13()*m31() + transform.m14()*m41();
		double t12 = transform.m11()*m12() + transform.m12()*m22() + transform.m13()*m32() + transform.m14()*m42();
		double t13 = transform.m11()*m13() + transform.m12()*m23() + transform.m13()*m33() + transform.m14()*m43();
		double t14 = transform.m11()*m14() + transform.m12()*m24() + transform.m13()*m34() + transform.m14()*m44();
		
		double t21 = transform.m21()*m11() + transform.m22()*m21() + transform.m23()*m31() + transform.m24()*m41();
		double t22 = transform.m21()*m12() + transform.m22()*m22() + transform.m23()*m32() + transform.m24()*m42();
		double t23 = transform.m21()*m13() + transform.m22()*m23() + transform.m23()*m33() + transform.m24()*m43();
		double t24 = transform.m21()*m14() + transform.m22()*m24() + transform.m23()*m34() + transform.m24()*m44();
		
		double t31 = transform.m31()*m11() + transform.m32()*m21() + transform.m33()*m31() + transform.m34()*m41();
		double t32 = transform.m31()*m12() + transform.m32()*m22() + transform.m33()*m32() + transform.m34()*m41();
		double t33 = transform.m31()*m13() + transform.m32()*m23() + transform.m33()*m33() + transform.m34()*m41();
		double t34 = transform.m31()*m14() + transform.m32()*m24() + transform.m33()*m34() + transform.m34()*m44();
		
		double t41 = transform.m41()*m11() + transform.m42()*m21() + transform.m43()*m31() + transform.m44()*m41();
		double t42 = transform.m41()*m12() + transform.m42()*m22() + transform.m43()*m32() + transform.m44()*m41();
		double t43 = transform.m41()*m13() + transform.m42()*m23() + transform.m43()*m33() + transform.m44()*m41();
		double t44 = transform.m41()*m14() + transform.m42()*m24() + transform.m43()*m34() + transform.m44()*m44();
		
		setTransform(t11, t21, t31, t41, t12, t22, t32, t42, t13, t23, t33, t43, t14, t24, t34, t44);
	}


	@Override
	public void transformView(Transform transform) {
		double t11 = m11()*transform.m11() + m12()*transform.m21() + m13()*transform.m31() + m14()*transform.m41();
		double t12 = m11()*transform.m12() + m12()*transform.m22() + m13()*transform.m32() + m14()*transform.m42();
		double t13 = m11()*transform.m13() + m12()*transform.m23() + m13()*transform.m33() + m14()*transform.m43();
		double t14 = m11()*transform.m14() + m12()*transform.m24() + m13()*transform.m34() + m14()*transform.m44();
		
		double t21 = m21()*transform.m11() + m22()*transform.m21() + m23()*transform.m31() + m24()*transform.m41();
		double t22 = m21()*transform.m12() + m22()*transform.m22() + m23()*transform.m32() + m24()*transform.m42();
		double t23 = m21()*transform.m13() + m22()*transform.m23() + m23()*transform.m33() + m24()*transform.m43();
		double t24 = m21()*transform.m14() + m22()*transform.m24() + m23()*transform.m34() + m24()*transform.m44();
		
		double t31 = m31()*transform.m11() + m32()*transform.m21() + m33()*transform.m31() + m34()*transform.m41();
		double t32 = m31()*transform.m12() + m32()*transform.m22() + m33()*transform.m32() + m34()*transform.m41();
		double t33 = m31()*transform.m13() + m32()*transform.m23() + m33()*transform.m33() + m34()*transform.m41();
		double t34 = m31()*transform.m14() + m32()*transform.m24() + m33()*transform.m34() + m34()*transform.m44();
		
		double t41 = m41()*transform.m11() + m42()*transform.m21() + m43()*transform.m31() + m44()*transform.m41();
		double t42 = m41()*transform.m12() + m42()*transform.m22() + m43()*transform.m32() + m44()*transform.m41();
		double t43 = m41()*transform.m13() + m42()*transform.m23() + m43()*transform.m33() + m44()*transform.m41();
		double t44 = m41()*transform.m14() + m42()*transform.m24() + m43()*transform.m34() + m44()*transform.m44();
		
		setTransform(t11, t21, t31, t41, t12, t22, t32, t42, t13, t23, t33, t43, t14, t24, t34, t44);
	}
	
	@Override
	public double transformX(double x, double y) {
		return x*m11() + y*m12() + 0.*m13() + m14();
	}
	
	@Override
	public double transformY(double x, double y) {
		return x*m21() + y*m22() + 0.*m23() + m24();
	}

	/**
	 * @return The matrix entry from the 1st row, 1st column.
	 */
	public abstract double m11();

	/**
	 * @param m11 Set the matrix entry from the 1st row, 1st column.
	 */
	protected abstract void setM11(double m11);

	/**
	 * @return The matrix entry from the 2nd row, 1st column.
	 */
	public abstract double m21();

	/**
	 * @param m21 Set the matrix entry from the 2nd row, 1st column.
	 */
	protected abstract void setM21(double m21);

	/**
	 * @return The matrix entry from the 3rd row, 1st column.
	 */
	public abstract double m31();

	/**
	 * @param m31 Set the matrix entry from the 3rd row, 1st column.
	 */
	protected abstract void setM31(double m31);

	/**
	 * @return The matrix entry from the 4th row, 1st column.
	 */
	public abstract double m41();

	/**
	 * @param m41 Set the matrix entry from the 4th row, 1st column.
	 */
	protected abstract void setM41(double m41);

	/**
	 * @return The matrix entry from the 1st row, 2nd column.
	 */
	public abstract double m12();

	/**
	 * @param m12 Set the matrix entry from the 1st row, 2nd column.
	 */
	protected abstract void setM12(double m12);

	/**
	 * @return The matrix entry from the 2nd row, 2nd column.
	 */
	public abstract double m22();

	/**
	 * @param m22 Set the matrix entry from the 2nd row, 2nd column.
	 */
	protected abstract void setM22(double m22);

	/**
	 * @return The matrix entry from the 3rd row, 2nd column.
	 */
	public abstract double m32();

	/**
	 * @param m32 Set the matrix entry from the 3rd row, 2nd column.
	 */
	protected abstract void setM32(double m32);

	/**
	 * @return The matrix entry from the 4th row, 2nd column.
	 */
	public abstract double m42();

	/**
	 * @param m42 Set the matrix entry from the 4th row, 2nd column.
	 */
	protected abstract void setM42(double m42);

	/**
	 * @return The matrix entry from the 1st row, 3rd column.
	 */
	public abstract double m13();

	/**
	 * @param m13 Set the matrix entry from the 1st row, 3rd column.
	 */
	protected abstract void setM13(double m13);

	/**
	 * @return The matrix entry from the 2nd row, 3rd column.
	 */
	public abstract double m23();

	/**
	 * @param m23 Set the matrix entry from the 2nd row, 3rd column.
	 */
	protected abstract void setM23(double m23);

	/**
	 * @return The matrix entry from the 3rd row, 3rd column.
	 */
	public abstract double m33();

	/**
	 * @param m33 Set the matrix entry from the 3rd row, 3rd column.
	 */
	protected abstract void setM33(double m33);

	/**
	 * @return The matrix entry from the 4th row, 3rd column.
	 */
	public abstract double m43();

	/**
	 * @param m43 Set the matrix entry from the 4th row, 3rd column.
	 */
	protected abstract void setM43(double m43);

	/**
	 * @return The matrix entry from the 1st row, 4th column.
	 */
	public abstract double m14();

	/**
	 * @param m14 Set the matrix entry from the 1st row, 4th column.
	 */
	protected abstract void setM14(double m14);

	/**
	 * @return The matrix entry from the 2nd row, 4th column.
	 */
	public abstract double m24();

	/**
	 * @param m24 Set the matrix entry from the 2nd row, 4th column.
	 */
	protected abstract void setM24(double m24);

	/**
	 * @return The matrix entry from the 3rd row, 4th column.
	 */
	public abstract double m34();

	/**
	 * @param m34 Set the matrix entry from the 3rd row, 4th column.
	 */
	protected abstract void setM34(double m34);

	/**
	 * @return The matrix entry from the 4th row, 4th column.
	 */
	public abstract double m44();

	/**
	 * @param m44 Set the matrix entry from the 4th row, 4th column.
	 */
	protected abstract void setM44(double m44);
	
}