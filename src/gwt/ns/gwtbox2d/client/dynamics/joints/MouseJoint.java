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

package gwt.ns.gwtbox2d.client.dynamics.joints;

import gwt.ns.gwtbox2d.client.common.Mat22;
import gwt.ns.gwtbox2d.client.common.Vec2;
import gwt.ns.gwtbox2d.client.common.XForm;
import gwt.ns.gwtbox2d.client.dynamics.Body;
import gwt.ns.gwtbox2d.client.dynamics.TimeStep;
import gwt.ns.gwtbox2d.client.pooling.TLMat22;
import gwt.ns.gwtbox2d.client.pooling.TLVec2;

//Updated to rev 56->130 of b2MouseJoint.cpp/.h

//p = attached point, m = mouse point
//C = p - m
//Cdot = v
//   = v + cross(w, r)
//J = [I r_skew]
//Identity used:
//w k % (rx i + ry j) = w * (-ry i + rx j)
//
public class MouseJoint extends Joint {

	public final Vec2 m_localAnchor;

	public final Vec2 m_target;

	public final Vec2 m_force;

	public final Mat22 m_mass; // effective mass for point-to-point constraint.

	public final Vec2 m_C; // position error

	public float m_maxForce;

	public float m_beta; // bias factor

	public float m_gamma; // softness

	public MouseJoint(final MouseJointDef def) {
		super(def);

		m_force = new Vec2();
		m_target = new Vec2();
		m_C = new Vec2();
		m_mass = new Mat22();
		m_target.set(def.target);
		m_localAnchor = XForm.mulTrans(m_body2.m_xf, m_target);

		m_maxForce = def.maxForce;

		final float mass = m_body2.m_mass;

		// Frequency
		final float omega = (float) (2.0f * Math.PI * def.frequencyHz);

		// Damping coefficient
		final float d = 2.0f * mass * def.dampingRatio * omega;

		// Spring stiffness
		final float k = mass * omega * omega;

		// magic formulas
		m_gamma = 1.0f / (d + def.timeStep * k);
		m_beta = def.timeStep * k / (d + def.timeStep * k);

	}

	/** Use this to update the target point. */
	public void setTarget(final Vec2 target) {
		if (m_body2.isSleeping()) {
			m_body2.wakeUp();
		}
		m_target.set(target);
	}

	@Override
	public Vec2 getAnchor1() {
		return m_target;
	}

	// djm pooled
	private static final TLVec2 tlanchor2 = new TLVec2();

	/**
	 * this comes from a pooled value
	 */
	@Override
	public Vec2 getAnchor2() {
		final Vec2 anchor2 = tlanchor2.get();
		m_body2.getWorldLocationToOut(m_localAnchor, anchor2);
		return anchor2;
	}

	// djm pooled
	private static final TLVec2 tlr = new TLVec2();
	private static final TLMat22 tlK1 = new TLMat22();
	private static final TLMat22 tlK2 = new TLMat22();

	@Override
	public void initVelocityConstraints(final TimeStep step) {
		final Body b = m_body2;

		final Vec2 r = tlr.get();
		final Mat22 K1 = tlK1.get();
		final Mat22 K2 = tlK2.get();
		
		// Compute the effective mass matrix.
		r.set(m_localAnchor);
		r.subLocal(b.getMemberLocalCenter());
		Mat22.mulToOut(b.m_xf.R, r, r);
		// Vec2 r = Mat22.mul(b.m_xf.R,
		// m_localAnchor.sub(b.getMemberLocalCenter()));

		// K = [(1/m1 + 1/m2) * eye(2) - skew(r1) * invI1 * skew(r1) - skew(r2)
		// * invI2 * skew(r2)]
		// = [1/m1+1/m2 0 ] + invI1 * [r1.y*r1.y -r1.x*r1.y] + invI2 *
		// [r1.y*r1.y -r1.x*r1.y]
		// [ 0 1/m1+1/m2] [-r1.x*r1.y r1.x*r1.x] [-r1.x*r1.y r1.x*r1.x]
		final float invMass = b.m_invMass;
		final float invI = b.m_invI;

		K1.set(invMass, 0.0f, 0.0f, invMass);

		K2.set(invI * r.y * r.y, -invI * r.x * r.y, -invI * r.x * r.y, invI
		       * r.x * r.x);

		// Mat22 K = K1.add(K2);
		K1.addLocal(K2);
		K1.col1.x += m_gamma;
		K1.col2.y += m_gamma;

		K1.invertToOut( m_mass);

		m_C.set(b.m_sweep.c.x + r.x - m_target.x, b.m_sweep.c.y + r.y
		        - m_target.y);

		// Cheat with some damping
		b.m_angularVelocity *= 0.98f;

		// Warm starting.
		final float Px = step.dt * m_force.x;
		final float Py = step.dt * m_force.y;
		b.m_linearVelocity.x += invMass * Px;
		b.m_linearVelocity.y += invMass * Py;
		b.m_angularVelocity += invI * (r.x * Py - r.y * Px);
	}

	@Override
	public boolean solvePositionConstraints() {
		return true;
	}

	// djm pooled, from above too
	private static final TLVec2 tlCdot = new TLVec2();
	private static final TLVec2 tlforce = new TLVec2();
	private static final TLVec2 tloldForce = new TLVec2();
	private static final TLVec2 tlP = new TLVec2();

	@Override
	public void solveVelocityConstraints(final TimeStep step) {
		final Body b = m_body2;
		
		final Vec2 r = tlr.get();
		final Vec2 Cdot = tlCdot.get();
		final Vec2 force = tlforce.get();
		final Vec2 oldForce = tloldForce.get();
		final Vec2 P = tlP.get();
		
		r.set(m_localAnchor);
		r.subLocal(b.getMemberLocalCenter());
		Mat22.mulToOut(b.m_xf.R, r, r);
		// Vec2 r = Mat22.mul(b.m_xf.R,
		// m_localAnchor.sub(b.getMemberLocalCenter()));

		// Cdot = v + cross(w, r)
		Vec2.crossToOut(b.m_angularVelocity, r, Cdot);
		Cdot.addLocal(b.m_linearVelocity);
		//Vec2 Cdot = b.m_linearVelocity.add(Vec2.cross(b.m_angularVelocity, r));

		// Vec2 force = -step.inv_dt * Mat22.mul(m_mass, Cdot + (m_beta *
		// step.inv_dt) * m_C + m_gamma * step.dt * m_force);
		/*Vec2 force = new Vec2(Cdot.x + (m_beta * step.inv_dt) * m_C.x + m_gamma
		 * step.dt * m_force.x, Cdot.y + (m_beta * step.inv_dt) * m_C.y
				+ m_gamma * step.dt * m_force.y);*/
		force.set(Cdot.x + (m_beta * step.inv_dt) * m_C.x + m_gamma
		          * step.dt * m_force.x, Cdot.y + (m_beta * step.inv_dt) * m_C.y
		          + m_gamma * step.dt * m_force.y);
		Mat22.mulToOut(m_mass, force, force);
		force.mulLocal(-step.inv_dt);

		oldForce.set(m_force);
		m_force.addLocal(force);
		final float forceMagnitude = m_force.length();
		if (forceMagnitude > m_maxForce) {
			m_force.mulLocal(m_maxForce / forceMagnitude);
		}
		force.set(m_force.x - oldForce.x, m_force.y - oldForce.y);

		P.x = step.dt * force.x;
		P.y = step.dt * force.y;

		b.m_angularVelocity += b.m_invI * Vec2.cross(r, P);
		b.m_linearVelocity.addLocal(P.mulLocal(b.m_invMass));
	}

	@Override
	public Vec2 getReactionForce() {
		return m_force;
	}

	@Override
	public float getReactionTorque() {
		return 0.0f;
	}
}

