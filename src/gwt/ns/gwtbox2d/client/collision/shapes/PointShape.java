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


package gwt.ns.gwtbox2d.client.collision.shapes;

import gwt.ns.gwtbox2d.client.collision.AABB;
import gwt.ns.gwtbox2d.client.collision.MassData;
import gwt.ns.gwtbox2d.client.collision.Segment;
import gwt.ns.gwtbox2d.client.collision.SegmentCollide;
import gwt.ns.gwtbox2d.client.common.Mat22;
import gwt.ns.gwtbox2d.client.common.RaycastResult;
import gwt.ns.gwtbox2d.client.common.Settings;
import gwt.ns.gwtbox2d.client.common.Vec2;
import gwt.ns.gwtbox2d.client.common.XForm;
import gwt.ns.gwtbox2d.client.pooling.TLVec2;

/**
 * Point shape.  Like a circle shape of zero radius, except
 * that it has a finite mass.
 *
 */
public class PointShape extends Shape {
	public final Vec2 m_localPosition;
	public float m_mass;

	public PointShape(final ShapeDef def) {
		super(def);
		assert(def.type == ShapeType.POINT_SHAPE);
		final PointDef pointDef = (PointDef)def;
		m_type = ShapeType.POINT_SHAPE;
		m_localPosition = pointDef.localPosition.clone();
		m_mass = pointDef.mass;
	}
	
	// djm gwt.ns.gwtbox2d.pooling
	private static final TLVec2 tlP = new TLVec2();
	/**
	 * @see Shape#computeAABB(AABB, XForm)
	 */
	@Override
	public void computeAABB(final AABB aabb, final XForm transform) {
		//Vec2 p = transform.position.add(Mat22.mul(transform.R, m_localPosition));
		final Vec2 p = tlP.get();
		Mat22.mulToOut(transform.R, m_localPosition, p);
		p.add(transform.position);
		aabb.lowerBound.set(p.x-Settings.EPSILON, p.y-Settings.EPSILON);
		aabb.upperBound.set(p.x+Settings.EPSILON, p.y+Settings.EPSILON);
	}

	/**
	 * @see Shape#computeMass(MassData)
	 */
	@Override
	public void computeMass(final MassData massData) {
		massData.mass = m_mass;
		massData.center.set(m_localPosition);
		massData.I = 0.0f;
	}

	// djm gwt.ns.gwtbox2d.pooling
	private static final TLVec2 tlSwept1 = new TLVec2();
	private static final TLVec2 tlSwept2 = new TLVec2();
	/**
	 * @see Shape#computeSweptAABB(AABB, XForm, XForm)
	 */
	@Override
	public void computeSweptAABB(final AABB aabb, final XForm transform1, final XForm transform2) {
		final Vec2 sweptP1 = tlSwept1.get();
		final Vec2 sweptP2 = tlSwept2.get();
		//Vec2 p1 = transform1.position.add(Mat22.mul(transform1.R, m_localPosition));
		//Vec2 p2 = transform2.position.add(Mat22.mul(transform2.R, m_localPosition));
		Mat22.mulToOut( transform2.R, m_localPosition, sweptP1);
		Mat22.mulToOut( transform2.R, m_localPosition, sweptP2);

		Vec2.minToOut( sweptP1, sweptP2, aabb.lowerBound);
		Vec2.maxToOut( sweptP1, sweptP2, aabb.upperBound);

		aabb.lowerBound.x -= Settings.EPSILON;
		aabb.lowerBound.y -= Settings.EPSILON;

		aabb.upperBound.x += Settings.EPSILON;
		aabb.upperBound.y += Settings.EPSILON;
	}

	/**
	 * @see Shape#testPoint(XForm, Vec2)
	 */
	@Override
	public boolean testPoint(final XForm xf, final Vec2 p) {
		// TODO djm: could use more optimization.
		// we could probably use bit shifting
		return false;
	}

	// Collision Detection in Interactive 3D Environments by Gino van den Bergen
	// From Section 3.1.2
	// x = s + a * r
	// norm(x) = radius
	// djm pooled
	private static final TLVec2 tlS = new TLVec2();
	private static final TLVec2 tlPosition = new TLVec2();
	private static final TLVec2 tlR = new TLVec2();
	/**
	 * @see Shape#testSegment(XForm, RaycastResult, Segment, float)
	 */
	@Override
	public SegmentCollide testSegment(final XForm xf, final RaycastResult out, final Segment segment, final float maxLambda){
		final Vec2 position = tlPosition.get();
		final Vec2 s = tlS.get();
		
		Mat22.mulToOut( xf.R, m_localPosition, position);
		position.addLocal(xf.position);
		s.set(segment.p1);
		s.subLocal(position);
		final float b = Vec2.dot(s, s);

		// Does the segment start inside the circle?
		if (b < 0.0f){
			return SegmentCollide.STARTS_INSIDE_COLLIDE;
		}

		final Vec2 r = tlR.get();

		// Solve quadratic equation.
		r.set(segment.p2).subLocal(segment.p1);
		final float c =  Vec2.dot(s, r);
		final float rr = Vec2.dot(r, r);
		final float sigma = c * c - rr * b;

		// Check for negative discriminant and short segment.
		if (sigma < 0.0f || rr < Settings.EPSILON){
			return SegmentCollide.MISS_COLLIDE;
		}

		// Find the point of intersection of the line with the circle.
		float a = (float) -(c + Math.sqrt(sigma));

		// Is the intersection point on the segment?
		if (0.0f <= a && a <= maxLambda * rr){
			a /= rr;
			out.lambda = a;
			out.normal.set(r).mulLocal(a).addLocal(s);
			out.normal.normalize();
			return SegmentCollide.HIT_COLLIDE;
		}

		return SegmentCollide.MISS_COLLIDE;
	}

	/**
	 * @see Shape#updateSweepRadius(Vec2)
	 */
	// djm optimized
	@Override
	public void updateSweepRadius(final Vec2 center) {
		//Vec2 d = m_localPosition.sub(center);
		final float dx = m_localPosition.x - center.x;
		final float dy = m_localPosition.y - center.y;
		m_sweepRadius = (float) (Math.sqrt(dx*dx + dy*dy) - Settings.toiSlop);
	}

	/**
	 * @return a copy of local position
	 */
	public Vec2 getLocalPosition() {
		return m_localPosition.clone();
	}

	/**
	 * This is the member variable for the local position.
	 * Don't change this.
	 * @return
	 */
	public Vec2 getMemberLocalPosition(){
		return m_localPosition;
	}

	public float getMass() {
		return m_mass;
	}

}
