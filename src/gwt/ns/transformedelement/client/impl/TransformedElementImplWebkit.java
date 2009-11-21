package gwt.ns.transformedelement.client.impl;

import gwt.ns.transformedelement.client.TransformedElement;
import gwt.ns.transforms.client.impl.TransformImplWebkit;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;

/**
 * Implementation of CSS transform for webkit based browsers
 * will only work in safari 4 up to when standard is implemented
 */
public class TransformedElementImplWebkit extends TransformedElement {
	protected Element target;
	protected Style targetStyle;
	
	@Override
	protected void initElement(Element elem) {
		// we can directly instantiate webkit css transform
		transform = new TransformImplWebkit();
		target = elem;
		targetStyle = target.getStyle();
	}

	@Override
	public void setTransform() {
		targetStyle.setProperty("WebkitTransform", get2dCssString());
	}

}
