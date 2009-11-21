package gwt.ns.transforms.client;

/**
 * this is the interface for Transform objects which internall use a matrix
 * Each entry in the 4x4 matrix is represented by m<row><column>.
 * For instance m12 represents the value in the 2nd column of the first row.
 * 
 * Note that this representation uses column vectors, meaning vectors are
 * transformed by multiplying them to the right of a tranformation matrix.
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
	
	/*
	 *	TODO:
		inverse (why? maybe wait until use case comes up for designing api)
		
		units?? ems, px, cm allowed in firefox at least...
		skew?
		shear?
		copy constructor? get copy at all?
		set transform origin (will need to store somehow for when full xform output to string)
		reset vs set to identity?
	*/
	
	/**
	 * Reset this transformation to the identity transform
	 */
	public abstract void setToIdentity();
	// not wild about that method signature, but got it from
	// java.awt.geom.AffineTransform so good for now
	
	
	/**
	 * set new values in the matrix
	 * the order is very specific, with each parameter specified first by
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
	public void set(double t11, double t21, double t31, double t41, double t12, double t22, double t32, double t42, double t13, double t23, double t33, double t43, double t14, double t24, double t34, double t44) {
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
	
	/**
	 * apply a transformation to current local coordinate system
	 * this is the slowest way to multiply (though for implementaiton in pure
	 * java/js most of this should be inlined and the performance will be the
	 * same). if you can do a mulitply in a superclass (or natively), do it
	 * 
	 * @param trans the transformation to apply
	 */
	public void multiplyLocal(Transform xform) {
		double t11 = xform.m11()*m11() + xform.m12()*m21() + xform.m13()*m31() + xform.m14()*m41();
		double t12 = xform.m11()*m12() + xform.m12()*m22() + xform.m13()*m32() + xform.m14()*m42();
		double t13 = xform.m11()*m13() + xform.m12()*m23() + xform.m13()*m33() + xform.m14()*m43();
		double t14 = xform.m11()*m14() + xform.m12()*m24() + xform.m13()*m34() + xform.m14()*m44();
		
		double t21 = xform.m21()*m11() + xform.m22()*m21() + xform.m23()*m31() + xform.m24()*m41();
		double t22 = xform.m21()*m12() + xform.m22()*m22() + xform.m23()*m32() + xform.m24()*m42();
		double t23 = xform.m21()*m13() + xform.m22()*m23() + xform.m23()*m33() + xform.m24()*m43();
		double t24 = xform.m21()*m14() + xform.m22()*m24() + xform.m23()*m34() + xform.m24()*m44();
		
		double t31 = xform.m31()*m11() + xform.m32()*m21() + xform.m33()*m31() + xform.m34()*m41();
		double t32 = xform.m31()*m12() + xform.m32()*m22() + xform.m33()*m32() + xform.m34()*m41();
		double t33 = xform.m31()*m13() + xform.m32()*m23() + xform.m33()*m33() + xform.m34()*m41();
		double t34 = xform.m31()*m14() + xform.m32()*m24() + xform.m33()*m34() + xform.m34()*m44();
		
		double t41 = xform.m41()*m11() + xform.m42()*m21() + xform.m43()*m31() + xform.m44()*m41();
		double t42 = xform.m41()*m12() + xform.m42()*m22() + xform.m43()*m32() + xform.m44()*m41();
		double t43 = xform.m41()*m13() + xform.m42()*m23() + xform.m43()*m33() + xform.m44()*m41();
		double t44 = xform.m41()*m14() + xform.m42()*m24() + xform.m43()*m34() + xform.m44()*m44();
		
		set(t11, t21, t31, t41, t12, t22, t32, t42, t13, t23, t33, t43, t14, t24, t34, t44);
	}
	
	/**
	 * Returns a new CssTransform set to the inverse of this CssTransform
	 * This CssTransform is unmodified.
	 * 
	 * @return a new CssTransform set to the inverse of this CssTransform
	 */
	// TODO:
	//public CssTransform getInverse();
	
	
	/**
	 * Returns a copy of this CssTransform. This matrix is unmodified.
	 * 
	 * @return a new copy of the CssTransform
	 */
	// TODO:
	//public CssTransform getCopy();

	/**
	 * apply a transformation to current view coordinate system
	 * this is the slowest way to multiply. if you can do a multiply
	 * in a superclass (or natively), do that
	 * 
	 * @param trans the transformation to apply
	 */
	public void multiplyView(Transform xform) {
		double t11 = m11()*xform.m11() + m12()*xform.m21() + m13()*xform.m31() + m14()*xform.m41();
		double t12 = m11()*xform.m12() + m12()*xform.m22() + m13()*xform.m32() + m14()*xform.m42();
		double t13 = m11()*xform.m13() + m12()*xform.m23() + m13()*xform.m33() + m14()*xform.m43();
		double t14 = m11()*xform.m14() + m12()*xform.m24() + m13()*xform.m34() + m14()*xform.m44();
		
		double t21 = m21()*xform.m11() + m22()*xform.m21() + m23()*xform.m31() + m24()*xform.m41();
		double t22 = m21()*xform.m12() + m22()*xform.m22() + m23()*xform.m32() + m24()*xform.m42();
		double t23 = m21()*xform.m13() + m22()*xform.m23() + m23()*xform.m33() + m24()*xform.m43();
		double t24 = m21()*xform.m14() + m22()*xform.m24() + m23()*xform.m34() + m24()*xform.m44();
		
		double t31 = m31()*xform.m11() + m32()*xform.m21() + m33()*xform.m31() + m34()*xform.m41();
		double t32 = m31()*xform.m12() + m32()*xform.m22() + m33()*xform.m32() + m34()*xform.m41();
		double t33 = m31()*xform.m13() + m32()*xform.m23() + m33()*xform.m33() + m34()*xform.m41();
		double t34 = m31()*xform.m14() + m32()*xform.m24() + m33()*xform.m34() + m34()*xform.m44();
		
		double t41 = m41()*xform.m11() + m42()*xform.m21() + m43()*xform.m31() + m44()*xform.m41();
		double t42 = m41()*xform.m12() + m42()*xform.m22() + m43()*xform.m32() + m44()*xform.m41();
		double t43 = m41()*xform.m13() + m42()*xform.m23() + m43()*xform.m33() + m44()*xform.m41();
		double t44 = m41()*xform.m14() + m42()*xform.m24() + m43()*xform.m34() + m44()*xform.m44();
		
		set(t11, t21, t31, t41, t12, t22, t32, t42, t13, t23, t33, t43, t14, t24, t34, t44);
	}

	@Override
	public void reset() {
		// TODO: currently reset to identity. see javadoc comment for future
		setToIdentity();
	}


	public abstract double m11();

	protected abstract void setM11(double m11);

	public abstract double m21();

	protected abstract void setM21(double m21);

	public abstract double m31();

	protected abstract void setM31(double m31);

	public abstract double m41();

	protected abstract void setM41(double m41);

	public abstract double m12();

	protected abstract void setM12(double m12);

	public abstract double m22();

	protected abstract void setM22(double m22);

	public abstract double m32();

	protected abstract void setM32(double m32);

	public abstract double m42();

	protected abstract void setM42(double m42);

	public abstract double m13();

	protected abstract void setM13(double m13);

	public abstract double m23();

	protected abstract void setM23(double m23);

	public abstract double m33();

	protected abstract void setM33(double m33);

	public abstract double m43();

	protected abstract void setM43(double m43);

	public abstract double m14();

	protected abstract void setM14(double m14);

	public abstract double m24();

	protected abstract void setM24(double m24);

	public abstract double m34();

	protected abstract void setM34(double m34);

	public abstract double m44();

	protected abstract void setM44(double m44);
}