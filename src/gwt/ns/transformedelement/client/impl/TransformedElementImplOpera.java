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

package gwt.ns.transformedelement.client.impl;

import gwt.ns.transformedelement.client.TransformedElement;

/**
 * Implementation of CSS transform for opera browsers > 10.5.
 * 
 * TODO: fallback to no-op for prior versions
 */
public class TransformedElementImplOpera extends TransformedElement {
	
	@Override
	public void commitTransform() {
		target.getStyle().setProperty("OTransform", get2dCssString());
	}

	/**
	 * Returns the 2 dimensional matrix transform function property per
	 * CSS3 2D Transforms Draft<br><br>
	 * 
	 * No length units on translation entries.
	 * 
	 * @see <a href="http://www.w3.org/TR/css3-2d-transforms/#transform-functions">CSS3 2D Transforms</a>
	 * 
	 * @return
	 */
	public String get2dCssString() {
		String tmp = "matrix(";
		tmp += toFixed(transform.m11()) + ", ";
		tmp += toFixed(transform.m21()) + ", ";
		tmp += toFixed(transform.m12()) + ", ";
		tmp += toFixed(transform.m22()) + ", ";
		tmp += toFixed(transform.m14()) + ", ";
		tmp += toFixed(transform.m24()) + ")";
		
		return tmp;
	}

	@Override
	public void setOrigin(double ox, double oy) {
		String origin = toFixed(ox, 0) + "px " + toFixed(oy, 0) + "px";
		target.getStyle().setProperty("OTransformOrigin", origin);
	}
}
