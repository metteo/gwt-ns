package gwt.ns.transformedelement.client.impl;

import gwt.ns.transformedelement.client.TransformedElement;

import com.google.gwt.dom.client.Element;

/**
 * Implementation of TransformedElement for browsers with no 2d css transforms
 * For now, do nothing.
 * TODO: maybe add translations with method similar to IE?
 */
public class TransformedElementImplDefault extends TransformedElement {

	@Override
	protected void initElement(Element elem) {
		
	}

	@Override
	public void setTransform() {
		
	}

	@Override
	public String get2dCssString() {
		return "";
	}

	@Override
	public void rotateLocal(double angle) {
	}

	@Override
	public void rotateAtPointLocal(double angle, double px, double py) {
	}

	@Override
	public void scaleLocal(double sx, double sy) {
	}

	@Override
	public void scaleAtPointLocal(double sx, double sy, double px, double py) {
	}

	@Override
	public void translateLocal(double tx, double ty) {
	}

	@Override
	public void reset() {
	}

	@Override
	public void rotateView(double angle) {
	}

	@Override
	public void rotateAtPointView(double angle, double px, double py) {
	}

	@Override
	public void scaleView(double sx, double sy) {
	}

	@Override
	public void scaleAtPointView(double sx, double sy, double px, double py) {
	}

	@Override
	public void translateView(double tx, double ty) {
	}

}
