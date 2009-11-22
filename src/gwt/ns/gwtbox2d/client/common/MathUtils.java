/*
 * This file has been modified from the original JBox2D source.
 * Original source license found below.
 * 
 * Modifications Copyright 2009 Brendan Kenny
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

/*
 * JBox2D - A Java Port of Erin Catto's Box2D
 * 
 * JBox2D homepage: http://jbox2d.sourceforge.net/
 * Box2D homepage: http://www.box2d.org
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 * claim that you wrote the original software. If you use this software
 * in a product, an acknowledgment in the product documentation would be
 * appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 * misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package gwt.ns.gwtbox2d.client.common;

/**
 * A few math methods that don't fit very well anywhere else. djm: added ToOut
 * method
 */
public class MathUtils {
	public final static float map(final float val, final float fromMin, final float fromMax,
			final float toMin, final float toMax) {
		final float mult = (val - fromMin) / (fromMax - fromMin);
		final float res = toMin + mult * (toMax - toMin);
		return res;
	}

	/** Returns the closest value to 'a' that is in between 'low' and 'high' */
	public final static float clamp(final float a, final float low, final float high) {
		return Math.max(low, Math.min(a, high));
	}

	/* djm optimized */
	public final static Vec2 clamp(final Vec2 a, final Vec2 low, final Vec2 high) {
		final Vec2 min = new Vec2();
		Vec2.minToOut(a, high, min);
		Vec2.maxToOut(low, min, min);
		return min;
	}

	/* djm created */
	public final static void clampToOut(final Vec2 a, final Vec2 low, final Vec2 high,
			final Vec2 dest) {
		Vec2.minToOut(a, high, dest);
		Vec2.maxToOut(low, dest, dest);
	}

	/**
	 * Next Largest Power of 2: Given a binary integer value x, the next largest
	 * power of 2 can be computed by a SWAR algorithm that recursively "folds"
	 * the upper bits into the lower bits. This process yields a bit vector with
	 * the same most significant 1 as x, but all 1's below it. Adding 1 to that
	 * value yields the next largest power of 2.
	 */
	public final static int nextPowerOfTwo(int x) {
		x |= x >> 1;
		x |= x >> 2;
		x |= x >> 4;
		x |= x >> 8;
		x |= x >> 16;
		return x + 1;
	}

	public final static boolean isPowerOfTwo(final int x) {
		return x > 0 && (x & x - 1) == 0;
	}
}