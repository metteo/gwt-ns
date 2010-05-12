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
 * An implementation of {@link Transform} for most WebKit browsers with
 * support for WebKitCSSMatrix until very recent changes. Here, WebKitCSSMatrix
 * was fully row-major, so multiplication needs to be reversed accordingly.
 */
class TransformWebKitRowMajor extends TransformWebKit {
	@Override
	public void multiply(Transform local) {
		CssMatrix localMatrix = ((TransformWebKitRowMajor) local).transform;
		
		transform = localMatrix.multiply(transform);
	}
	
	@Override
	public void multiply(Transform view, Transform local) {
		CssMatrix viewMatrix = ((TransformWebKitRowMajor) view).transform;
		CssMatrix localMatrix = ((TransformWebKitRowMajor) local).transform;
		
		transform = localMatrix.multiply(viewMatrix);
	}

	@Override
	public void multiplyView(Transform view) {
		CssMatrix viewMatrix = ((TransformWebKitRowMajor) view).transform;
		
		transform = transform.multiply(viewMatrix);
	}
	
	@Override
	public void skewX(double theta) {
		// for some reason this isn't implemented on (at least) Windows yet
		CssMatrix tmp = createNewMatrix();
		tmp.setM21(Math.tan(theta));
		transform = tmp.multiply(transform);
	}

	@Override
	public void skewY(double theta) {
		// for some reason this isn't implemented on (at least) Windows yet
		CssMatrix tmp = createNewMatrix();
		tmp.setM12(Math.tan(theta));
		transform = tmp.multiply(transform);
	}
}
