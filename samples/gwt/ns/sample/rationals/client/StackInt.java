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

package gwt.ns.sample.rationals.client;

import java.util.Stack;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayInteger;

/**
 * A very simple int stack interface to allow use of pure java for development
 * mode and inlinable JavaScript for production mode.
 */
public abstract class StackInt {
	// Looking forward to the Lightweight Collections framework
	public static final StackInt create() {
		return GWT.isScript() ? new JsStackInt() : new DevIntStack();
	}
	
	public abstract boolean isEmpty();
	
	public abstract int pop();
	
	public abstract void push(int value);
}

/**
 * Wrapper around JRE Stack implementation. Allows development
 * mode to stay out of JSNI.
 */
class DevIntStack extends StackInt {
	Stack<Integer> stack = new Stack<Integer>();
	
	@Override
	public boolean isEmpty() {
		return stack.empty();
	}

	@Override
	public int pop() {
		return stack.pop();
	}

	@Override
	public void push(int value) {
		stack.push(value); // autobox assemble
	}
}

/**
 * Simple JsArrayInteger-based stack.
 */
class JsStackInt extends StackInt {
	JsArrayInteger stack = createArray();

	private static final native JsArrayInteger createArray()  /*-{
		return [];
  	}-*/;
	
	@Override
	public final boolean isEmpty() {
		return stack.length() == 0;
	}
	
	@Override
	public final int pop() {
    	return stack.shift();
	}

	@Override
	public void push(int value) {
		stack.unshift(value);
	}
}
