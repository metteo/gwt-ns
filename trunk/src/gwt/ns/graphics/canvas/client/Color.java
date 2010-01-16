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

/**
 * A CSS color string creation class which offers extremely basic sanity
 * checking to create valid color strings. The goal here is to explicitly
 * rely on the user agent or excanvas to handle different color value formats.
 * 
 * <p>A hexadecimal color String is already in the correct format. Colors by
 * keywords are not included here to cut down on compiled size. Current
 * browsers support all (or almost all) of the colors listed here:
 * <a href='http://www.w3.org/TR/css3-color/#svg-color'>W3C Color Keywords</a>.</p>
 * 
 * @see <a href='http://www.w3.org/TR/css3-color/'>CSS3 Color Working Draft</a>
 * @see <a href='https://developer.mozilla.org/en/CSS/color_value'>Mozilla Developer Center: Color Value</a>
 */
public class Color {
	private String colorString;
	
	/**
	 * Specifies a Color object in HSL coordinates. Parameters will be clamped
	 * to the interval indicated.
	 * 
	 * @param h Hue as an integer in the interval [0, 360]
	 * @param s Saturation as an integer in the interval [0, 100]
	 * @param l Lightness as an integer in the interval [0, 100]
	 * @return Object representing specified color
	 */
	public static Color parseHsl(int h, int s, int l) {
		h = (h < 0 ? 0 : h) > 360 ? 360 : h;
		s = (s < 0 ? 0 : s) > 100 ? 100 : s;
		l = (l < 0 ? 0 : l) > 100 ? 100 : l;
		
		Color col = new Color();
		col.colorString =  "hsl(" + h + "," + s + "," + l + ")";
		return col;
	}
	
	/**
	 * Specifies a Color object in HSLa coordinates.
	 * 
	 * @param h Hue as an integer in the interval [0, 360]
	 * @param s Saturation as an integer in the interval [0, 100]
	 * @param l Lightness as an integer in the interval [0, 100]
	 * @param a Alpha (transparency) as a floating point value in the
	 *   interval [0, 1]
	 * @return Object representing specified color
	 */
	public static Color parseHsla(int h, int s, int l, double a) {
		h = (h < 0 ? 0 : h) > 360 ? 360 : h;
		s = (s < 0 ? 0 : s) > 100 ? 100 : s;
		l = (l < 0 ? 0 : l) > 100 ? 100 : l;
		a = (a < 0. ? 0. : a) > 1. ? 1. : a;
		
		Color col = new Color();
		col.colorString =  "hsl(" + h + "," + s + "," + l + "," + a + ")";
		return col;
	}
	
	/**
	 * Specifies a Color object in RGB coordinates (sRGB space).
	 * 
	 * @param r Red as an integer in the interval [0, 255]
	 * @param g Green as an integer in the interval [0, 255]
	 * @param b Blue as an integer in the interval [0, 255]
	 * @return Object representing specified color
	 */
	public static Color parseRgb(int r, int g, int b) {
		r = (r < 0 ? 0 : r) > 255 ? 255 : r;
		g = (g < 0 ? 0 : g) > 255 ? 255 : g;
		b = (b < 0 ? 0 : b) > 255 ? 255 : b;
		
		Color col = new Color();
		col.colorString =  "rgb(" + r + "," + g + "," + b + ")";
		return col;
	}
	
	/**
	 * Specifies a Color object in RGBa coordinates (sRGB space).
	 * 
	 * @param r Red as an integer in the interval [0, 255]
	 * @param g Green as an integer in the interval [0, 255]
	 * @param b Blue as an integer in the interval [0, 255]
	 * @param a Alpha (transparency) as a floating point value in the
	 *   interval [0, 1]
	 * @return Object representing specified color
	 */
	public static Color parseRgba(int r, int g, int b, double a) {
		r = (r < 0 ? 0 : r) > 255 ? 255 : r;
		g = (g < 0 ? 0 : g) > 255 ? 255 : g;
		b = (b < 0 ? 0 : b) > 255 ? 255 : b;
		a = (a < 0. ? 0. : a) > 1. ? 1. : a;
		
		Color col = new Color();
		col.colorString =  "rgba(" + r + "," + g + "," + b + "," + a + ")";
		return col;
	}

	/**
	 * @return A CSS &lt;color&gt; value.
	 */
	@Override
	public String toString() {
		return colorString;
	}
}
