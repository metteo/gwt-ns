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

package gwt.ns.transforms.client;

/*
 *	TODO:
	inverse (why? maybe wait until use case comes up for designing api)
	
	units?? ems, px, cm allowed in firefox at least...
	shear?
	copy constructor? get copy at all?
	set transform origin (will need to store somehow for when full xform output to string)
	reset vs set to identity?
*/
public interface Transformable {

	/**
	 * Rotation in <em>local</em> (transformed) coordinates by 
	 * angle theta.<br><br>
	 * <strong>Note:</strong> due to definition of screen coordinates
	 * (with positive y pointing down), positive values of theta rotate
	 * <em>clockwise</em>.
	 * 
	 * @param angle The angle to rotate, in degrees
	 */
	public void rotate(double angle);

	/**
	 * Rotate in local coordinates around point (px, py) by angle theta.
	 * 
	 * @param angle The angle to rotate in degrees
	 * @param px The x-coordinate of origin of rotation, in local coordinatess
	 * @param py The y-coordinate of origin of rotation, in local coordinates
	 */
	public void rotateAtPoint(double angle, double px, double py);

	/**
	 * Scale in <em>local</em> (transformed) coordinates by vector (sx, sy).
	 * 
	 * @param sx The scaling along the local x-axis
	 * @param sy The scaling along the local y-axis
	 */
	public void scale(double sx, double sy);

	/**
	 * Scale in local coordinates, expanding from point (px, py) by
	 * vector(sx, sy).
	 * 
	 * @param sx The scaling along the local x-axis
	 * @param sy The scaling along the local y-axis
	 * @param px The x-coordinate of origin of scaling, in local coordinates
	 * @param py The y-coordinate of origin of scaling, in local coordinates
	 */
	public void scaleAtPoint(double sx, double sy, double px, double py);

	/**
	 * Translation in <em>local</em> (transformed) coordinates by vector (tx, ty).
	 * 
	 * @param tx The translation along local x-axis
	 * @param ty The translation along local y-axis
	 */
	public void translate(double tx, double ty);

	/**
	 * Rotation in <em>view</em> coordinates by angle theta.<br><br>
	 * <strong>Note:</strong> due to definition of screen coordinates
	 * (with positive y pointing down), positive values of theta rotate
	 * <em>clockwise</em>.
	 * 
	 * @param angle The angle to rotate in degrees
	 */
	public void rotateView(double angle);

	/**
	 * Rotate, in <em>view</em> coordinates, around point (px, py) by
	 * angle theta.
	 * 
	 * @param angle The angle to rotate in degrees
	 * @param px The x-coordinate of origin of rotation in view coordinates
	 * @param py The y-coordinate of origin of rotation in view coordinates
	 */
	public void rotateAtPointView(double angle, double px, double py);

	/**
	 * Scale in <em>view</em> coordinates by vector (sx, sy).
	 * 
	 * @param sx The scaling along view x-axis
	 * @param sy The scaling along view y-axis
	 */
	public void scaleView(double sx, double sy);

	/**
	 * Scale by vector(sx, sy), in <em>view</em> coordinates, expanding from
	 * point (px, py).
	 * 
	 * @param sx The scaling along view x-axis
	 * @param sy The scaling along view y-axis
	 * @param px The x-coordinate of origin of scaling, in view coordinates
	 * @param py The y-coordinate of origin of scaling, in view coordinates
	 */
	public void scaleAtPointView(double sx, double sy, double px, double py);

	/**
	 * Translation in <em>view</em> coordinates by vector (tx, ty).
	 * 
	 * @param tx The translation along view x-axis
	 * @param ty The translation along view y-axis
	 */
	public void translateView(double tx, double ty);
	
	
	/**
	 * Reset object to original transformation.
	 * <br>
	 * Currently resets to identity.<br>
	 * Could, for e.g. CSS Tranforms, reset to whatever original
	 * styling transform was
	 */
	public void reset();
	
	/**
	 * Skews <em>local</em> (transformed) coordinates around the x-axis by 
	 * the given angle.
	 * 
	 * @param angle The skew angle.
	 */
	public void skewX(double angle);
	
	/**
	 * Skews <em>local</em> (transformed) coordinates around the y-axis by
	 * the given angle.
	 * 
	 * @param angle The skew angle.
	 */
	public void skewY(double angle);
	
	/**
	 * Skews <em>view</em> coordinates around the x-axis by the given angle.
	 * 
	 * @param angle The skew angle.
	 */
	public void skewXView(double angle);
	
	/**
	 * Skews <em>view</em> coordinates around the y-axis by the given angle.
	 * 
	 * @param angle The skew angle.
	 */
	public void skewYView(double angle);

}