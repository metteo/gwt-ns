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

import com.google.gwt.dom.client.Style;
import gwt.ns.transformedelement.client.TransformedElement;
import gwt.ns.transforms.client.Transform;
import gwt.ns.transforms.client.impl.TransformImplWebkit;
import gwt.ns.transforms.client.impl.WebKitCssMatrix;

/**
 * Implementation of CSS transform for webkit based browsers
 * for safari 4 until standard is implemented
 */
public class TransformedElementImplWebkit extends TransformedElement {

	@Override
	public void commitTransform() {
		webkitCommit(target.getStyle(), ((TransformImplWebkit)transform).getJsoMatrix());
	}
	
	@Override
	protected Transform createTransform() {
		// this overrules transform module's deferred binding, but allows
		// us to have superfast dom writes by skipping string property building
		return new TransformImplWebkit();
	}
	
	protected static final native void webkitCommit(Style style, WebKitCssMatrix matrix) /*-{
		style.WebkitTransform = matrix;
	}-*/;
}
