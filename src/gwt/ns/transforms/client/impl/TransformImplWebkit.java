package gwt.ns.transforms.client.impl;

import gwt.ns.transforms.client.Transform;


/**
 * Webkit Implementation of a CSS transformation.
 * Uses native functionality of Safari 4+ for matrix manipulations
 * see {@link WebKitCssMatrix} for further details
 * 
 * <em>Note:</em> WebKitCSSMatrix treats vectors as rows (sometimes called
 * row-major ordering), so this class adjusts where necessary to convert to
 * column major. This means local transforms happen on the left and view
 * transforms happen on the right. accessors (m11()) and setters (setM11())
 * access and set the transpose of what is implied by the method signature.
 * The result should be a column-major matrix to the user.
 *
 */
public class TransformImplWebkit extends Transform {
	private WebKitCssMatrix transform = WebKitCssMatrix.newInstance();
	
	/**
	 * Construct a new 3D transform using webkit's native functionality,
	 * set to identity
	 */
	public TransformImplWebkit() { }
	
	@Override
	public void rotateLocal(double angle) {
		transform = transform.rotate(angle);
	}

	@Override
	public void rotateAtPointLocal(double angle, double px, double py) {
		// TODO: optimize this to reduce matrix creations and arithmetic.
		// possibly refactor to combine with viewRotateAtPoint
		transform = transform.translate(px, py).rotate(angle).translate(-px, -py);
	}

	@Override
	public void scaleLocal(double sx, double sy) {
		transform = transform.scale(sx, sy);
	}

	@Override
	public void scaleAtPointLocal(double sx, double sy, double px, double py) {
		// TODO: optimize this to reduce matrix creations and arithmetic,
		// possibly refactor to combine with viewScaleAtPoint
		transform = transform.translate(px, py).scale(sx, sy).translate(-px, -py);
	}

	@Override
	public void translateLocal(double tx, double ty) {
		transform = transform.translate(tx, ty);

	}

	@Override
	public void rotateView(double angle) {
		WebKitCssMatrix rot = WebKitCssMatrix.newInstance().rotate(angle);
		transform = transform.viewMultiply(rot);
	}

	@Override
	public void rotateAtPointView(double angle, double px, double py) {
		// TODO: optimize this to reduce matrix creations and arithmetic.
		// possibly refactor to combine with userRotateAtPoint
		// TODO: check the order of ops on rot. should they be reversed?
		WebKitCssMatrix rot = WebKitCssMatrix.newInstance();
		rot = rot.translate(px, py).rotate(angle).translate(-px, -py);
		
		transform = transform.viewMultiply(rot);
	}

	@Override
	public void scaleView(double sx, double sy) {
		WebKitCssMatrix scale = WebKitCssMatrix.newInstance().scale(sx, sy);
		transform = transform.viewMultiply(scale);
	}

	@Override
	public void scaleAtPointView(double sx, double sy, double px, double py) {
		// TODO: optimize this to reduce matrix creations and arithmetic,
		// possibly refactor to combine with userScaleAtPoint
		// TODO: check the order of ops on scale. should they be reversed?
		WebKitCssMatrix scale = WebKitCssMatrix.newInstance();
		scale = scale.translate(px, py).scale(sx, sy).translate(-px, -py);
		
		transform = transform.viewMultiply(scale);
	}

	@Override
	public void translateView(double tx, double ty) {
		WebKitCssMatrix trans = WebKitCssMatrix.newInstance().translate(tx, ty);
		transform = transform.viewMultiply(trans);
	}

	@Override
	public void setToIdentity() {
		transform.setToIdentity();
	}
	
	@Override
	public double m11() {
		return transform.m11();
	}

	@Override
	public double m12() {
		return transform.m21();
	}

	@Override
	public double m13() {
		return transform.m31();
	}

	@Override
	public double m14() {
		return transform.m41();
	}

	@Override
	public double m21() {
		return transform.m12();
	}

	@Override
	public double m22() {
		return transform.m22();
	}

	@Override
	public double m23() {
		return transform.m32();
	}

	@Override
	public double m24() {
		return transform.m42();
	}

	@Override
	public double m31() {
		return transform.m13();
	}

	@Override
	public double m32() {
		return transform.m23();
	}

	@Override
	public double m33() {
		return transform.m33();
	}

	@Override
	public double m34() {
		return transform.m43();
	}

	@Override
	public double m41() {
		return transform.m14();
	}

	@Override
	public double m42() {
		return transform.m24();
	}

	@Override
	public double m43() {
		return transform.m34();
	}

	@Override
	public double m44() {
		return transform.m44();
	}

	@Override
	protected void setM11(double m11) {
		transform.setM11(m11);
	}

	@Override
	protected void setM12(double m12) {
		transform.setM21(m12);
	}

	@Override
	protected void setM13(double m13) {
		transform.setM31(m13);
	}

	@Override
	protected void setM14(double m14) {
		transform.setM41(m14);
	}

	@Override
	protected void setM21(double m21) {
		transform.setM12(m21);
	}

	@Override
	protected void setM22(double m22) {
		transform.setM22(m22);
	}

	@Override
	protected void setM23(double m23) {
		transform.setM32(m23);
	}

	@Override
	protected void setM24(double m24) {
		transform.setM42(m24);
	}

	@Override
	protected void setM31(double m31) {
		transform.setM13(m31);
	}

	@Override
	protected void setM32(double m32) {
		transform.setM23(m32);
	}

	@Override
	protected void setM33(double m33) {
		transform.setM33(m33);
	}

	@Override
	protected void setM34(double m34) {
		transform.setM43(m34);
	}

	@Override
	protected void setM41(double m41) {
		transform.setM14(m41);
	}

	@Override
	protected void setM42(double m42) {
		transform.setM24(m42);
	}

	@Override
	protected void setM43(double m43) {
		transform.setM34(m43);
	}

	@Override
	protected void setM44(double m44) {
		transform.setM44(m44);
	}
}
