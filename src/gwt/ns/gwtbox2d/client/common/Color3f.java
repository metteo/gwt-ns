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
 * Similar to javax.vecmath.Color3f holder
 * @author ewjordan
 *
 */
public class Color3f {
	
	public static final Color3f WHITE = new Color3f(255, 255, 255);
	public static final Color3f BLACK = new Color3f(0, 0, 0);
	public static final Color3f BLUE = new Color3f(0, 0, 255);
	public static final Color3f GREEN = new Color3f(0, 255, 0);
	public static final Color3f RED = new Color3f(255, 0, 0);
	
	public float x;
	public float y;
	public float z;

	public Color3f(float r, float g, float b) {
		x = r;
		y = g;
		z = b;
	}
}
