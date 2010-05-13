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

package gwt.ns.transformedelement.client;

/**
 * WebKit implementation of a transformed element. 3d transforms
 * orthographically projected to two implicitly unless a perspective projection
 * has been added manually. Note that, even when it has, many platforms will
 * currently ignore it.
 */
class TransformedElementWebKit extends TransformedElement {

	@Override
	public void commitTransform() {
		// native WebKitCSSMatrix will handle serialization
		// 2d/3d distinction is irrelevant. it will choose which is appropriate
		target.getStyle().setProperty("WebkitTransform", transform.toCss3dTransformString());
	}

	@Override
	public void setOriginPercentage(double ox, double oy) {
		String origin = toFixed(ox, 2) + "% " + toFixed(oy, 2) + "%";
		target.getStyle().setProperty("WebkitTransformOrigin", origin);
	}

	@Override
	public void setOriginPixels(double ox, double oy) {
		String origin = toFixed(ox, 0) + "px " + toFixed(oy, 0) + "px";
		target.getStyle().setProperty("WebkitTransformOrigin", origin);
	}

}
