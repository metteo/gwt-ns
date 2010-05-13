/*
 * Copyright 2010 Brendan Kenny
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package gwt.ns.demo.transformtree.client;

import gwt.ns.transformedelement.client.TransformedElement;
import gwt.ns.transforms.client.Transform;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * A widget for those times when you really need some dancing trees
 */
public class TreesWidget extends Composite {
	private static TreesWidgetUiBinder uiBinder = GWT
		.create(TreesWidgetUiBinder.class);
	interface TreesWidgetUiBinder extends UiBinder<Widget, TreesWidget> { }
	
	static final double ANIMATION_PERIOD = 1200;
	static final double FPS = 30;
	static final double SCALE_MAX = 1.06;
	static final double SCALE_MIN = .95;
	static final double SKEW_BIG_LEFT = .06;
	static final double SKEW_BIG_RIGHT = -.10;
	static final double SKEW_LITTLE_LEFT = .07;
	static final double SKEW_LITTLE_RIGHT = -.14;
	static final double SHADOW_DAMPENING_FACTOR = 8.;
	
	@UiField DivElement bigTreeDiv;
	@UiField DivElement bigShadowDiv;
	@UiField DivElement littleTreeDiv;
	@UiField DivElement littleShadowDiv;
	
	// Element wrappers to manage transformations
	TransformedElement bigTree;
	TransformedElement bigShadow;
	TransformedElement littleTree;
	TransformedElement littleShadow;
	
	// a stand-alone transform to store a shadow projection
	Transform shadowTransform = Transform.create();
	
	// animation timer
	Timer t;

	public TreesWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		
		// create TransformedElement objects
		bigTree = TransformedElement.wrap(bigTreeDiv);
		littleTree = TransformedElement.wrap(littleTreeDiv);
		bigShadow = TransformedElement.wrap(bigShadowDiv);
		littleShadow = TransformedElement.wrap(littleShadowDiv);
		
		// set transformation origin to bottom of elements for convenience
		bigTree.setOriginPercentage(50, 100);
		littleTree.setOriginPercentage(50, 100);
		bigShadow.setOriginPercentage(50, 100);
		littleShadow.setOriginPercentage(50, 100);
		
		// a transform to create something resembling a shadow on the ground
		shadowTransform.translate(0, -10);
		shadowTransform.skewX(-1.122);
		shadowTransform.scale(1, -.12);
		
		t = new Timer() {
			@Override
			public void run() {
				update();
			}
		};
		t.scheduleRepeating((int) (1000 / FPS));
		
		// init transforms so they are set before elements are drawn to screen
		update();
	}
	
	/**
	 * Update the transforms of the trees as a function of the current time
	 */
	public void update() {
		/*
		 * Clearly _custom_ animations, even for four elements, gets rather
		 * involved. To scale to even a few more than this, another
		 * approach will be needed. For something easily describable
		 * algorithmically, like a clock, this approach would be fine.
		 */
		
		double currentTime = Duration.currentTimeMillis() % ANIMATION_PERIOD;
		
		// scaling
		// trig function provides automatic periodicity and easing
		double scaleT = (1 + Math.cos(currentTime * (4*Math.PI / ANIMATION_PERIOD))) / 2.;
		double scaleY = lerp(SCALE_MIN, SCALE_MAX, scaleT);
		double scaleX = 1. / scaleY; // preserve area
		
		// skew function has half the frequency of scaling
		// skewing is asymmetric and slightly different per figure
		double skewT = Math.cos(currentTime * (2*Math.PI / ANIMATION_PERIOD));
		double bigSkewAngle = 0;
		double littleSkewAngle = 0;
		if (skewT < 0) {
			// left side. skew parameter moved to [0, 1]
			bigSkewAngle = lerp(SKEW_BIG_LEFT, 0, skewT+1);
			littleSkewAngle = lerp(SKEW_LITTLE_LEFT, 0, skewT+1);
			
		} else {
			// right side
			bigSkewAngle = lerp(0, SKEW_BIG_RIGHT, skewT);
			littleSkewAngle = lerp(0, SKEW_LITTLE_RIGHT, skewT);
		}
		
		// clear and apply new transforms to trees
		// TransformedElement will manage actually writing to element's style
		bigTree.setToIdentityTransform();
		bigTree.scale(scaleX, scaleY);
		bigTree.skewX(bigSkewAngle);
		
		littleTree.setToIdentityTransform();
		littleTree.scale(scaleX, scaleY);
		littleTree.skewX(littleSkewAngle);
		
		// shadows are transformed based on the dampened transforms of trees
		bigSkewAngle /= SHADOW_DAMPENING_FACTOR;
		littleSkewAngle /= SHADOW_DAMPENING_FACTOR;
		scaleX = (scaleX + (SHADOW_DAMPENING_FACTOR-1)) / SHADOW_DAMPENING_FACTOR;
		scaleY = (scaleY + (SHADOW_DAMPENING_FACTOR-1)) / SHADOW_DAMPENING_FACTOR;
		
		// first, reset transform to shadow projection
		// then, apply scale and skew within that space
		bigShadow.setTransform(shadowTransform);
		bigShadow.scale(scaleX, scaleY);
		bigShadow.skewX(bigSkewAngle);
		
		littleShadow.setTransform(shadowTransform);
		littleShadow.scale(scaleX, scaleY);
		littleShadow.skewX(littleSkewAngle);
	}
	
	/**
	 * Linearly interpolates between values a and b by parameter t.
	 */
	static double lerp(double a, double b, double t) {
		return a + t*(b - a);
	}
}
