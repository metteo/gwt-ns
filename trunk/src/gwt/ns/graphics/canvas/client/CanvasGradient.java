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

package gwt.ns.graphics.canvas.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * An overlay of the CanvasGradient Javascript object. Create a CanvasGradient
 * by calling . 
 * <p>Gradients initially have no color until stops are placed along
 * it to define how the colors are distributed along the gradient. The color of
 * the gradient at each stop is the color specified for that stop. Between each
 * such stop, the colors and the alpha component are linearly interpolated over
 * the RGBA space without premultiplying the alpha value to find the color to
 * use at that offset. Before the first stop, the color will be the color of
 * the first stop. After the last stop, the color must be the color of the last
 * stop. When there are no stops, the gradient is transparent black.</p>
 * 
 * @see <a href='http://www.whatwg.org/specs/web-apps/current-work/multipage/the-canvas-element.htmlhttp://www.whatwg.org/specs/web-apps/current-work/multipage/the-canvas-element.html#interpolation'>Canvas Gradient Spec</a>
 */
public class CanvasGradient extends JavaScriptObject {
	// protected JSO constructor
	protected CanvasGradient() { }
	
	/**
	 * Adds a color to a specific stop in the gradient. The offset must be in
	 * the interval [0,1] and defines the relative position of the color in the
	 * gradient. An offset outside of that interval or a color that cannot be
	 * parsed will throw a Javascript exception.
	 * 
	 * @param offset The relative position of the color in the gradient
	 * @param color A valid CSS color
	 */
	// TODO: expand for Color types
	public final native void addColorStop(double offset, String color) /*-{
		this.addColorStop(offset, color);
	}-*/;
}
