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

package gwt.ns.transformedelement.client.impl;

import gwt.ns.transformedelement.client.TransformedElement;

/**
 * Implementation of CSS transform for webkit based browsers
 * will only work in safari 4 up to when standard is implemented
 */
public class TransformedElementImplWebkit extends TransformedElement {

	@Override
	public void commitTransform() {
		target.getStyle().setProperty("WebkitTransform", get2dCssString());
	}
	
	/**
	 * Returns the 2 dimensional matrix transform function property per
	 * CSS3 2D Transforms Draft<br><br>
	 * 
	 * Specifies the current 2D transformation in the form of an augmented
	 * 2x2 transformation matrix and translation vector.<br><br>
	 * 
	 * It's not completely clear who will prevail on the subject of a length unit for
	 * the translation vector. It makes sense in other contexts, but doesn't make
	 * complete sense in the midst of a bunch of other unitless numbers.<br><br>
	 * 
	 * Regardless, currently, firefox needs a unit, webkit does not.<br><br>
	 * 
	 * @see <a href="http://www.w3.org/TR/css3-2d-transforms/#transform-functions">CSS3 2D Transforms</a>
	 * 
	 * @return
	 */
	public String get2dCssString() {
		StringBuilder tmp = new StringBuilder("matrix(");
		tmp.append(toFixed(transform.m11())).append(", ");
		tmp.append(toFixed(transform.m21())).append(", ");
		tmp.append(toFixed(transform.m12())).append(", ");
		tmp.append(toFixed(transform.m22())).append(", ");
		tmp.append(toFixed(transform.m14())).append(", ");
		tmp.append(toFixed(transform.m24())).append(")");
		
		return tmp.toString();
	}
}
