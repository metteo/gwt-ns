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

package gwt.ns.graphics.canvas.client.impl;

import com.google.gwt.dom.client.Element;

public interface CanvasElementFactory {
	/**
	 * Create a Canvas element. If this is IE, initialize the excanvas library
	 * for Canvas emulation.
	 * 
	 * @return A Canvas Element
	 */
	public Element createCanvas(int width, int height);
}
