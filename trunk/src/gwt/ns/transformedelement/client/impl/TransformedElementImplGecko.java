package gwt.ns.transformedelement.client.impl;

import gwt.ns.transformedelement.client.TransformedElement;
import gwt.ns.transforms.client.impl.TransformImplDefault;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;

/**
 * Implementation of CSS transform for gecko browsers
 * will only work in 1.9.1 up to when standard is implemented
 */
public class TransformedElementImplGecko extends TransformedElement {
	protected Element target;
	protected Style targetStyle;
	
	@Override
	protected void initElement(Element elem) {
		// we can directly instantiate default transform
		transform = new TransformImplDefault();
		target = elem;
		targetStyle = target.getStyle();
	}

	@Override
	public void setTransform() {
		targetStyle.setProperty("MozTransform", get2dCssString());
	}

}
