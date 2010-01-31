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

import com.google.gwt.core.client.JsArrayInteger;

import gwt.ns.json.client.Json;
import gwt.ns.webworker.client.IncrementalWorkerEntryPoint;

public class RationalsWorker extends IncrementalWorkerEntryPoint {
	static final int START_INDEX = 1; // set to 100000000 for taxing execution
	JsArrayInteger stack = createIntStack();
	
	// jre collection for representative hosted mode performance
	//Stack<Integer> stack = new Stack<Integer>();
	int numerator;
	int denominator;
	int index;
	
	static native JsArrayInteger createIntStack() /*-{
      return [];
	}-*/;
	
	public void postRational(int numerator, int denominator) {
		String data = Json.strigify(RationalNumber.create(numerator, denominator));
		postMessage(data);
	}
	
	@Override
	public int execute() {
		// we must preempt ourselves to support platforms without native Workers
		stack.unshift(index++);
		numerator = denominator;
		denominator = 0;
		
		while(stack.length() != 0) { //!stack.empty()) {
			int cur = stack.shift();//.pop();
			
			if (cur == 0 || cur == 1) {
				// base cases
				denominator++;
				
			} else if (cur % 2 != 0) {
				// odd case
				stack.unshift((cur - 1) / 2);
				
			} else {
				// even
				cur /= 2;
				stack.unshift(cur);
				stack.unshift(cur - 1);
			}
		}
		
		postRational(numerator, denominator);
		
		// run the next execution as soon as possible
		return 0;
	}

	@Override
	public void onWorkerLoad() {
		// initial values
		index = START_INDEX;
		numerator = 1;
		denominator = 1;
	}

}
