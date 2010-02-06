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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A javascript object representing a rational number.
 */
public class RationalNumber extends JavaScriptObject {
	protected RationalNumber() {
		// required protected constructor for JavaScriptObject
	}
	
	/**
	 * @param numerator
	 * @param denominator
	 * @return A new rational number
	 */
	public static final native RationalNumber create(int numerator, int denominator) /*-{
		return { "n" : numerator, "d" : denominator };
  	}-*/;
	
	/**
	 * @return The denominator of this rational number
	 */
	public final native int getDenominator() /*-{
		return this.d;
	}-*/;
	
	/**
	 * @return The numerator of this rational number
	 */
	public final native int getNumerator() /*-{
		return this.n;
  	}-*/;
}
