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

package gwt.ns.gwtbox2d.client.dynamics;

import gwt.ns.gwtbox2d.client.collision.AABB;
import gwt.ns.gwtbox2d.client.collision.BroadPhase;
import gwt.ns.gwtbox2d.client.collision.Segment;
import gwt.ns.gwtbox2d.client.collision.SegmentCollide;
import gwt.ns.gwtbox2d.client.collision.SortKeyFunc;
import gwt.ns.gwtbox2d.client.collision.TOI;
import gwt.ns.gwtbox2d.client.collision.shapes.Shape;
import gwt.ns.gwtbox2d.client.common.RaycastResult;
import gwt.ns.gwtbox2d.client.common.Settings;
import gwt.ns.gwtbox2d.client.common.Vec2;
import gwt.ns.gwtbox2d.client.dynamics.contacts.Contact;
import gwt.ns.gwtbox2d.client.dynamics.contacts.ContactEdge;
import gwt.ns.gwtbox2d.client.dynamics.controllers.Controller;
import gwt.ns.gwtbox2d.client.dynamics.controllers.ControllerDef;
import gwt.ns.gwtbox2d.client.dynamics.controllers.ControllerEdge;
import gwt.ns.gwtbox2d.client.dynamics.joints.Joint;
import gwt.ns.gwtbox2d.client.dynamics.joints.JointDef;
import gwt.ns.gwtbox2d.client.dynamics.joints.JointEdge;
import gwt.ns.gwtbox2d.client.pooling.TLTimeStep;
import gwt.ns.gwtbox2d.client.pooling.stacks.IslandStack;
import gwt.ns.gwtbox2d.client.pooling.stacks.TimeStepStack;

import java.util.ArrayList;



//Updated to rev 56->118->142->150 of b2World.cpp/.h

/**
 * The world that physics takes place in.
 * <BR><BR>
 * To the extent that it is possible, avoid accessing members
 * directly, as in a future version their accessibility may
 * be rolled back - as un-Java as that is, we must follow
 * upstream C++ conventions, and for now everything is public
 * to speed development of Box2d, but it is subject to change.
 * You're warned!
 */
public class World {
	boolean m_lock;

	BroadPhase m_broadPhase;

	ContactManager m_contactManager;

	Body m_bodyList;

	/** Do not access, won't be useful! */
	Contact m_contactList;

	Joint m_jointList;
	
	Controller m_controllerList;
	
	int m_controllerCount;

	int m_bodyCount;

	int m_contactCount;

	int m_jointCount;

	Vec2 m_gravity;

	boolean m_allowSleep;

	Body m_groundBody;

	int m_positionIterationCount;

	/** Should we apply position correction? */
	boolean m_positionCorrection;
	/** Should we use warm-starting?  Improves stability in stacking scenarios. */
	boolean m_warmStarting;
	/** Should we enable continuous gwt.ns.gwtbox2d.collision detection? */
	boolean m_continuousPhysics;

	DestructionListener m_destructionListener;
	BoundaryListener m_boundaryListener;
	ContactFilter m_contactFilter;
	ContactListener m_contactListener;

	private float m_inv_dt0;

	private final ArrayList<Steppable> postStepList;

	/** Get the number of bodies. */
	public int getBodyCount() {
		return m_bodyCount;
	}

	/** Get the number of joints. */
	public int getJointCount() {
		return m_jointCount;
	}

	/** Get the number of contacts (each may have 0 or more contact points). */
	public int getContactCount() {
		return m_contactCount;
	}

	/** Change the global gravity vector. */
	public void setGravity(final Vec2 gravity) {
		m_gravity = gravity;
	}

	/** Get a clone of the global gravity vector.
	 * @return Clone of gravity vector
	 */
	public Vec2 getGravity() {
		return m_gravity.clone();
	}

	/** The world provides a single static ground body with no gwt.ns.gwtbox2d.collision shapes.
	 *	You can use this to simplify the creation of joints and static shapes.
	 */
	public Body getGroundBody() {
		return m_groundBody;
	}

	/**
	 * Get the world body list. With the returned body, use Body.getNext() to get
	 * the next body in the world list. A NULL body indicates the end of the list.
	 * @return the head of the world body list.
	 */
	public Body getBodyList() {
		return m_bodyList;
	}

	/**
	 * Get the world joint list. With the returned joint, use Joint.getNext() to get
	 * the next joint in the world list. A NULL joint indicates the end of the list.
	 * @return the head of the world joint list.
	 */
	public Joint getJointList() {
		return m_jointList;
	}

	/**
	 * Construct a world object.
	 * @param worldAABB a bounding box that completely encompasses all your shapes.
	 * @param gravity the world gravity vector.
	 * @param doSleep improve performance by not simulating inactive bodies.
	 */
	public World(final AABB worldAABB, final Vec2 gravity, final boolean doSleep) {
		m_positionCorrection = true;
		m_warmStarting = true;
		m_continuousPhysics = true;
		m_destructionListener = null;
		m_boundaryListener = null;
		m_contactFilter = ContactFilter.DEFAULT_FILTER;//&b2_defaultFilter;
		m_contactListener = null;

		m_inv_dt0 = 0.0f;

		m_bodyList = null;
		m_contactList = null;
		m_jointList = null;
		m_controllerList = null;

		m_bodyCount = 0;
		m_contactCount = 0;
		m_jointCount = 0;
		m_controllerCount = 0;

		m_lock = false;

		m_allowSleep = doSleep;

		m_gravity = gravity;

		m_contactManager = new ContactManager();
		m_contactManager.m_world = this;
		m_broadPhase = new BroadPhase(worldAABB, m_contactManager);

		final BodyDef bd = new BodyDef();
		m_groundBody = createBody(bd);
		postStepList = new ArrayList<Steppable>();
	}

	/** Register a destruction listener. */
	public void setDestructionListener(final DestructionListener listener) {
		m_destructionListener = listener;
	}

	/** Register a broad-phase boundary listener. */
	public void setBoundaryListener(final BoundaryListener listener) {
		m_boundaryListener = listener;
	}

	/** Register a contact event listener */
	public void setContactListener(final ContactListener listener) {
		m_contactListener = listener;
	}


	/**
	 *  Register a contact filter to provide specific control over gwt.ns.gwtbox2d.collision.
	 *  Otherwise the default filter is used (b2_defaultFilter).
	 */
	public void setContactFilter(final ContactFilter filter) {
		m_contactFilter = filter;
	}


	/**
	 * Create a body given a definition. No reference to the definition
	 * is retained.  Body will be static unless mass is nonzero.
	 * <BR><em>Warning</em>: This function is locked during callbacks.
	 */
	public Body createBody(final BodyDef def) {
		assert(m_lock == false);
		if (m_lock == true) {
			return null;
		}

		final Body b = new Body(def, this);

		// Add to world doubly linked list.
		b.m_prev = null;
		b.m_next = m_bodyList;
		if (m_bodyList != null) {
			m_bodyList.m_prev = b;
		}
		m_bodyList = b;
		++m_bodyCount;

		return b;
	}

	/**
	 * Destroy a rigid body given a definition. No reference to the definition
	 * is retained. This function is locked during callbacks.
	 * <BR><em>Warning</em>: This automatically deletes all associated shapes and joints.
	 * <BR><em>Warning</em>: This function is locked during callbacks.
	 */
	public void destroyBody(final Body b) {
		assert(m_bodyCount > 0);
		assert(m_lock == false);
		if (m_lock == true) {
			return;
		}

		// Delete the attached joints.
		JointEdge jn = b.m_jointList;
		while (jn != null) {
			final JointEdge jn0 = jn;
			jn = jn.next;

			if (m_destructionListener != null){
				m_destructionListener.sayGoodbye(jn0.joint);
			}

			destroyJoint(jn0.joint);
		}
		
		//Detach controllers attached to this body
		ControllerEdge ce = b.m_controllerList;
		while(ce != null) {
			ControllerEdge ce0 = ce;
			ce = ce.nextController;

			ce0.controller.removeBody(b);
		}

		// Delete the attached shapes. This destroys broad-phase
		// proxies and pairs, leading to the destruction of contacts.
		Shape s = b.m_shapeList;
		while (s != null) {
			final Shape s0 = s;
			s = s.m_next;

			if (m_destructionListener != null) {
				m_destructionListener.sayGoodbye(s0);
			}

			s0.destroyProxy(m_broadPhase);
			Shape.destroy(s0);
		}

		// Remove world body list.
		if (b.m_prev != null) {
			b.m_prev.m_next = b.m_next;
		}

		if (b.m_next != null) {
			b.m_next.m_prev = b.m_prev;
		}

		if (b == m_bodyList) {
			m_bodyList = b.m_next;
		}

		--m_bodyCount;
		//b->~b2Body();
	}

	/**
	 * Create a joint to constrain bodies together. No reference to the definition
	 * is retained. This may cause the connected bodies to cease colliding.
	 * <BR><em>Warning</em> This function is locked during callbacks.
	 */
	public Joint createJoint(final JointDef def) {
		assert(m_lock == false);

		final Joint j = Joint.create(def);

		// Connect to the world list.
		j.m_prev = null;
		j.m_next = m_jointList;
		if (m_jointList != null) {
			m_jointList.m_prev = j;
		}
		m_jointList = j;
		++m_jointCount;

		// Connect to the bodies' doubly linked lists
		j.m_node1.joint = j;
		j.m_node1.other = j.m_body2;
		j.m_node1.prev = null;
		j.m_node1.next = j.m_body1.m_jointList;
		if (j.m_body1.m_jointList != null) {
			j.m_body1.m_jointList.prev = j.m_node1;
		}
		j.m_body1.m_jointList = j.m_node1;

		j.m_node2.joint = j;
		j.m_node2.other = j.m_body1;
		j.m_node2.prev = null;
		j.m_node2.next = j.m_body2.m_jointList;
		if (j.m_body2.m_jointList != null) {
			j.m_body2.m_jointList.prev = j.m_node2;
		}
		j.m_body2.m_jointList = j.m_node2;

		// If the joint prevents collisions, then reset gwt.ns.gwtbox2d.collision filtering
		if (def.collideConnected == false) {
			// Reset the proxies on the body with the minimum number of shapes.
			final Body b = def.body1.m_shapeCount < def.body2.m_shapeCount ? def.body1
			                                                               : def.body2;
			for (Shape s = b.m_shapeList; s != null; s = s.m_next) {
				s.refilterProxy(m_broadPhase, b.getMemberXForm());
			}
		}

		return j;
	}

	/**
	 * Destroy a joint. This may cause the connected bodies to begin colliding.
	 * <BR><em>Warning</em>: This function is locked during callbacks.
	 */
	public void destroyJoint(final Joint j) {
		assert(m_lock == false);

		final boolean collideConnected = j.m_collideConnected;

		// Remove from the doubly linked list.
		if (j.m_prev != null) {
			j.m_prev.m_next = j.m_next;
		}

		if (j.m_next != null) {
			j.m_next.m_prev = j.m_prev;
		}

		if (j == m_jointList) {
			m_jointList = j.m_next;
		}

		// Disconnect from island graph.
		final Body body1 = j.m_body1;
		final Body body2 = j.m_body2;

		// Wake up connected bodies.
		body1.wakeUp();
		body2.wakeUp();

		// Remove from body 1
		if (j.m_node1.prev != null) {
			j.m_node1.prev.next = j.m_node1.next;
		}

		if (j.m_node1.next != null) {
			j.m_node1.next.prev = j.m_node1.prev;
		}

		if (j.m_node1 == body1.m_jointList) {
			body1.m_jointList = j.m_node1.next;
		}

		j.m_node1.prev = null;
		j.m_node1.next = null;

		// Remove from body 2
		if (j.m_node2.prev != null) {
			j.m_node2.prev.next = j.m_node2.next;
		}

		if (j.m_node2.next != null) {
			j.m_node2.next.prev = j.m_node2.prev;
		}

		if (j.m_node2 == body2.m_jointList) {
			body2.m_jointList = j.m_node2.next;
		}

		j.m_node2.prev = null;
		j.m_node2.next = null;

		Joint.destroy(j);

		assert m_jointCount > 0;
		--m_jointCount;

		// If the joint prevents collisions, then reset gwt.ns.gwtbox2d.collision filtering.
		if (collideConnected == false) {
			// Reset the proxies on the body with the minimum number of shapes.
			final Body b = body1.m_shapeCount < body2.m_shapeCount ? body1 : body2;
			for (Shape s = b.m_shapeList; s != null; s = s.m_next) {
				s.refilterProxy(m_broadPhase, b.getMemberXForm());
			}
		}
	}
	
	public Controller createController( final ControllerDef def) {
		Controller controller = def.create();

		controller.m_next = m_controllerList;
		controller.m_prev = null;

		if (m_controllerList != null) {
			m_controllerList.m_prev = controller;
		}
		
		m_controllerList = controller;
		++m_controllerCount;

		controller.m_world = this;

		return controller;
	}
	
	public void destroyController(Controller controller) {
		assert(m_controllerCount>0);
		
		if(controller.m_next != null)
		{
			controller.m_next.m_prev = controller.m_prev;
		}

		if(controller.m_prev != null)
		{
			controller.m_prev.m_next = controller.m_next;
		}

		if(controller == m_controllerList)
		{
			m_controllerList = controller.m_next;
		}

		--m_controllerCount;
	}

	// djm gwt.ns.gwtbox2d.pooling
	private static final TLTimeStep tlStep = new TLTimeStep();
	/**
	 * Take a time step. This performs gwt.ns.gwtbox2d.collision detection, integration,
	 * and constraint solution.
	 * @param dt the amount of time to simulate, this should not vary.
	 * @param iterations the number of iterations to be used by the constraint solver.
	 */
	public void step(final float dt, final int iterations) {
		m_lock = true;

		final TimeStep step = tlStep.get();
		step.dt = dt;
		step.maxIterations	= iterations;
		if (dt > 0.0f) {
			step.inv_dt = 1.0f / dt;
		} else {
			step.inv_dt = 0.0f;
		}

		step.dtRatio = m_inv_dt0 * dt;

		step.positionCorrection = m_positionCorrection;
		step.warmStarting = m_warmStarting;

		// Update contacts.
		m_contactManager.collide();

		// Integrate velocities, solve velocity constraints, and integrate positions.
		if (step.dt > 0.0f) {
			solve(step);
		}

		// Handle TOI events.
		if (m_continuousPhysics && step.dt > 0.0f) {
			solveTOI(step);
		}

		m_inv_dt0 = step.inv_dt;
		m_lock = false;
		
		postStep(dt,iterations);
	}


	/** Goes through the registered postStep functions and calls them. */
	private void postStep(final float dt, final int iterations) {
		for (final Steppable s:postStepList) {
			s.step(dt,iterations);
		}
	}

	/**
	 * Registers a Steppable object to be stepped
	 * immediately following the physics step, once
	 * the locks are lifted.
	 * @param s
	 */
	public void registerPostStep(final Steppable s) {
		postStepList.add(s);
	}

	/**
	 * Unregisters a method from post-stepping.
	 * Fails silently if method is not found.
	 * @param s
	 */
	public void unregisterPostStep(final Steppable s) {
		if (postStepList != null) {
			postStepList.remove(s);
		}
	}

	/** Re-filter a shape. This re-runs contact filtering on a shape. */
	public void refilter(final Shape shape) {
		shape.refilterProxy(m_broadPhase, shape.getBody().getMemberXForm());
	}

	/**
	 * Query the world for all shapes that potentially overlap the
	 * provided AABB up to max count.
	 * The number of shapes found is returned.
	 * @param aabb the query box.
	 * @param maxCount the capacity of the shapes array.
	 * @return array of shapes overlapped, up to maxCount in length
	 */
	public Shape[] query(final AABB aabb, final int maxCount) {
		final Object[] objs = m_broadPhase.query(aabb, maxCount);
		final Shape[] ret = new Shape[objs.length];
		System.arraycopy(objs, 0, ret, 0, objs.length);
		//for (int i=0; i<ret.length; ++i) {
		//	ret[i] = (Shape)(objs[i]);
		//}

		return ret;
	}


	//--------------- Internals Below -------------------
	// Internal yet public to make life easier.

	// Java note: sorry, guys, we have to keep this stuff public until
	// the C++ version does otherwise so that we can maintain the engine...

	// djm gwt.ns.gwtbox2d.pooling
	private static final IslandStack islands = new IslandStack();
	
	/** For internal use */
	public void solve(final TimeStep step) {
		m_positionIterationCount = 0;
		
		// Step all controllers
		for(Controller controller = m_controllerList; controller != null; controller = controller.m_next) {
			controller.step(step);
		}

		// Size the island for the worst case.
		final Island island = islands.get();
		island.init(m_bodyCount, m_contactCount, m_jointCount, m_contactListener);

		// Clear all the island flags.
		for (Body b = m_bodyList; b != null; b = b.m_next) {
			b.m_flags &= ~Body.e_islandFlag;
		}
		for (Contact c = m_contactList; c != null; c = c.m_next) {
			c.m_flags &= ~Contact.e_islandFlag;
		}
		for (Joint j = m_jointList; j != null; j = j.m_next) {
			j.m_islandFlag = false;
		}

		// Build and simulate all awake islands.
		final int stackSize = m_bodyCount;
		final Body[] stack = new Body[stackSize];
		for (Body seed = m_bodyList; seed != null; seed = seed.m_next) {
			if ( (seed.m_flags & (Body.e_islandFlag | Body.e_sleepFlag | Body.e_frozenFlag)) > 0){
				continue;
			}

			if (seed.isStatic()) {
				continue;
			}

			// Reset island and stack.
			island.clear();
			int stackCount = 0;
			stack[stackCount++] = seed;
			seed.m_flags |= Body.e_islandFlag;

			// Perform a depth first search (DFS) on the constraint graph.
			while (stackCount > 0) {
				// Grab the next body off the stack and add it to the island.
				final Body b = stack[--stackCount];
				island.add(b);

				// Make sure the body is awake.
				b.m_flags &= ~Body.e_sleepFlag;

				// To keep islands as small as possible, we don't
				// propagate islands across static bodies.
				if (b.isStatic()) {
					continue;
				}

				// Search all contacts connected to this body.
				for ( ContactEdge cn = b.m_contactList; cn != null; cn = cn.next) {
					// Has this contact already been added to an island?
					if ( (cn.contact.m_flags & (Contact.e_islandFlag | Contact.e_nonSolidFlag)) > 0) {
						continue;
					}

					// Is this contact touching?
					if (cn.contact.getManifoldCount() == 0) {
						continue;
					}

					island.add(cn.contact);
					cn.contact.m_flags |= Contact.e_islandFlag;

					// Was the other body already added to this island?
					final Body other = cn.other;
					if ((other.m_flags & Body.e_islandFlag) > 0) {
						continue;
					}

					assert stackCount < stackSize;
					stack[stackCount++] = other;
					other.m_flags |= Body.e_islandFlag;
				}

				// Search all joints connect to this body.
				for ( JointEdge jn = b.m_jointList; jn != null; jn = jn.next) {
					if (jn.joint.m_islandFlag == true) {
						continue;
					}

					island.add(jn.joint);
					jn.joint.m_islandFlag = true;

					final Body other = jn.other;
					if ((other.m_flags & Body.e_islandFlag) > 0) {
						continue;
					}

					assert (stackCount < stackSize);
					stack[stackCount++] = other;
					other.m_flags |= Body.e_islandFlag;
				}
			}

			island.solve(step, m_gravity, m_positionCorrection, m_allowSleep);

			m_positionIterationCount = Math.max(m_positionIterationCount, Island.m_positionIterationCount);

			// Post solve cleanup.
			for (int i = 0; i < island.m_bodyCount; ++i) {
				// Allow static bodies to participate in other islands.
				final Body b = island.m_bodies[i];
				if (b.isStatic()) {
					b.m_flags &= ~Body.e_islandFlag;
				}
			}
		}

		//m_broadPhase.commit();

		// Synchronize shapes, check for out of range bodies.
		for (Body b = m_bodyList; b != null; b = b.getNext()) {
			if ( (b.m_flags & (Body.e_sleepFlag | Body.e_frozenFlag)) != 0) {
				continue;
			}

			if (b.isStatic()) {
				continue;
			}

			// Update shapes (for broad-phase). If the shapes go out of
			// the world AABB then shapes and contacts may be destroyed,
			// including contacts that are
			final boolean inRange = b.synchronizeShapes();

			// Did the body's shapes leave the world?
			if (inRange == false && m_boundaryListener != null) {
				m_boundaryListener.violation(b);
			}
		}

		// Commit shape proxy movements to the broad-phase so that new contacts are created.
		// Also, some contacts can be destroyed.
		m_broadPhase.commit();
		
		islands.recycle(island);
	}

	
	// djm gwt.ns.gwtbox2d.pooling
	private static final TimeStepStack steps = new TimeStepStack();
	
	/** For internal use: find TOI contacts and solve them. */
	public void solveTOI(final TimeStep step) {
		// Reserve an island and a stack for TOI island solution.
		// djm do we always have to make a new island? or can we make
		// it static?
		
		// Size the island for the worst case.
		final Island island = islands.get();
		island.init(m_bodyCount, Settings.maxTOIContactsPerIsland, Settings.maxTOIJointsPerIsland, m_contactListener);

		//Simple one pass queue
		//Relies on the fact that we're only making one pass
		//through and each body can only be pushed/popped once.
		//To push:
		//  queue[queueStart+queueSize++] = newElement
		//To pop:
		//	poppedElement = queue[queueStart++];
		//  --queueSize;
		final int queueCapacity = m_bodyCount;
		final Body[] queue = new Body[queueCapacity];

		for (Body b = m_bodyList; b != null; b = b.m_next) {
			b.m_flags &= ~Body.e_islandFlag;
			b.m_sweep.t0 = 0.0f;
		}

		for (Contact c = m_contactList; c != null; c = c.m_next) {
			// Invalidate TOI
			c.m_flags &= ~(Contact.e_toiFlag | Contact.e_islandFlag);
		}

		for (Joint j = m_jointList; j != null; j = j.m_next) {
			j.m_islandFlag = false;
		}

		// Find TOI events and solve them.
		while (true) {
			// Find the first TOI.
			Contact minContact = null;
			float minTOI = 1.0f;

			for (Contact c = m_contactList; c != null; c = c.m_next) {
				if ((c.m_flags & (Contact.e_slowFlag | Contact.e_nonSolidFlag)) != 0) {
					continue;
				}

				// TODO_ERIN keep a counter on the contact, only respond to M TOIs per contact.
				float toi = 1.0f;
				if ((c.m_flags & Contact.e_toiFlag) != 0) {
					// This contact has a valid cached TOI.
					toi = c.m_toi;
				} else {
					// Compute the TOI for this contact.
					final Shape s1 = c.getShape1();
					final Shape s2 = c.getShape2();
					final Body b1 = s1.getBody();
					final Body b2 = s2.getBody();

					if ((b1.isStatic() || b1.isSleeping()) && (b2.isStatic() || b2.isSleeping())) {
						continue;
					}

					// Put the sweeps onto the same time interval.
					float t0 = b1.m_sweep.t0;

					if (b1.m_sweep.t0 < b2.m_sweep.t0) {
						t0 = b2.m_sweep.t0;
						b1.m_sweep.advance(t0);
					} else if (b2.m_sweep.t0 < b1.m_sweep.t0) {
						t0 = b1.m_sweep.t0;
						b2.m_sweep.advance(t0);
					}
					assert(t0 < 1.0f);

					// Compute the time of impact.
					toi = TOI.timeOfImpact(c.m_shape1, b1.m_sweep, c.m_shape2, b2.m_sweep);
					//System.out.println(toi);
					assert(0.0f <= toi && toi <= 1.0f);

					if (toi > 0.0f && toi < 1.0f) {
						toi = Math.min((1.0f - toi) * t0 + toi, 1.0f);
					}

					c.m_toi = toi;
					c.m_flags |= Contact.e_toiFlag;
				}

				if (Settings.EPSILON < toi && toi < minTOI) {
					// This is the minimum TOI found so far.
					minContact = c;
					minTOI = toi;

				}


			}

			if (minContact == null || 1.0f - 100.0f * Settings.EPSILON < minTOI) {
				// No more TOI events. Done!
				break;
			}

			// Advance the bodies to the TOI.
			final Shape s1 = minContact.getShape1();
			final Shape s2 = minContact.getShape2();
			final Body b1 = s1.getBody();
			final Body b2 = s2.getBody();
			b1.advance(minTOI);
			b2.advance(minTOI);

			// The TOI contact likely has some new contact points.
			minContact.update(m_contactListener);
			minContact.m_flags &= ~Contact.e_toiFlag;

			if (minContact.getManifoldCount() == 0) {
				// This shouldn't happen. Numerical error?
				//b2Assert(false);
				continue;
			}

			// Build the TOI island. We need a dynamic seed.
			Body seed = b1;
			if (seed.isStatic()) {
				seed = b2;
			}

			// Reset island and queue.
			island.clear();
			//int stackCount = 0;
			int queueStart = 0; //starting index for queue
			int queueSize = 0;  //elements in queue
			queue[queueStart+queueSize++] = seed;
			seed.m_flags |= Body.e_islandFlag;

			// Perform a breadth first search (BFS) on the contact/joint graph.
			while (queueSize > 0) {
				// Grab the head body off the queue and add it to the island.
				final Body b = queue[queueStart++];
				--queueSize;

				island.add(b);

				// Make sure the body is awake.
				b.m_flags &= ~Body.e_sleepFlag;

				// To keep islands as small as possible, we don't
				// propagate islands across static bodies.
				if (b.isStatic()) {
					continue;
				}

				// Search all contacts connected to this body.
				for (ContactEdge cn = b.m_contactList; cn != null; cn = cn.next) {
					// Does the TOI island still have space for contacts?
					if (island.m_contactCount == island.m_contactCapacity) {
						continue;
					}

					// Has this contact already been added to an island? Skip slow or non-solid contacts.
					if ( (cn.contact.m_flags & (Contact.e_islandFlag | Contact.e_slowFlag | Contact.e_nonSolidFlag)) != 0) {
						continue;
					}

					// Is this contact touching? For performance we are not updating this contact.
					if (cn.contact.getManifoldCount() == 0) {
						continue;
					}

					island.add(cn.contact);
					cn.contact.m_flags |= Contact.e_islandFlag;
					// Update other body.
					final Body other = cn.other;

					// Was the other body already added to this island?
					if ((other.m_flags & Body.e_islandFlag) != 0) {
						continue;
					}

					// March forward, this can do no harm since this is the min TOI.
					if (other.isStatic() == false) {
						other.advance(minTOI);
						other.wakeUp();
					}

					//push to the queue
					assert(queueSize < queueCapacity);
					queue[queueStart+queueSize++] = other;
					other.m_flags |= Body.e_islandFlag;

				}

				// Search all joints connect to this body.
				for ( JointEdge jn = b.m_jointList; jn != null; jn = jn.next) {
					if (island.m_jointCount == island.m_jointCapacity) {
						continue;
					}

					if (jn.joint.m_islandFlag == true) {
						continue;
					}

					island.add(jn.joint);

					jn.joint.m_islandFlag = true;

					final Body other = jn.other;
					if ((other.m_flags & Body.e_islandFlag) > 0) {
						continue;
					}

					if (other.isStatic() == false) {
						//System.out.println(minTOI);
						other.advance(minTOI);
						other.wakeUp();
					}

					assert (queueSize < queueCapacity);
					queue[queueStart+queueSize++] = other;
					other.m_flags |= Body.e_islandFlag;
				}

			}

			final TimeStep subStep = steps.get();
			subStep.warmStarting = false;
			subStep.dt = (1.0f - minTOI) * step.dt;
			assert(subStep.dt > Settings.EPSILON);
			subStep.inv_dt = 1.0f / subStep.dt;
			subStep.maxIterations = step.maxIterations;

			island.solveTOI(subStep);
			steps.recycle(subStep);
			
			// Post solve cleanup.
			for (int i = 0; i < island.m_bodyCount; ++i) {
				// Allow bodies to participate in future TOI islands.
				final Body b = island.m_bodies[i];
				b.m_flags &= ~Body.e_islandFlag;

				if ( (b.m_flags & (Body.e_sleepFlag | Body.e_frozenFlag)) != 0) {
					continue;
				}

				if (b.isStatic()) {
					continue;
				}

				// Update shapes (for broad-phase). If the shapes go out of
				// the world AABB then shapes and contacts may be destroyed,
				// including contacts that are
				final boolean inRange = b.synchronizeShapes();

				// Did the body's shapes leave the world?
				if (inRange == false && m_boundaryListener != null) {
					m_boundaryListener.violation(b);
				}

				// Invalidate all contact TOIs associated with this body. Some of these
				// may not be in the island because they were not touching.
				for (ContactEdge cn = b.m_contactList; cn != null; cn = cn.next) {
					cn.contact.m_flags &= ~Contact.e_toiFlag;
				}

			}

			for (int i = 0; i < island.m_contactCount; ++i) {
				// Allow contacts to participate in future TOI islands.

				final Contact c = island.m_contacts[i];
				c.m_flags &= ~(Contact.e_toiFlag | Contact.e_islandFlag);
			}

			for (int i=0; i < island.m_jointCount; ++i) {
				final Joint j = island.m_joints[i];
				j.m_islandFlag = false;
			}

			// Commit shape proxy movements to the broad-phase so that new contacts are created.
			// Also, some contacts can be destroyed.
			m_broadPhase.commit();
		}
		islands.recycle(island);
	}

	/** Enable/disable warm starting. For testing. */
	public void setWarmStarting(final boolean flag) { m_warmStarting = flag; }

	/** Enable/disable position correction. For testing. */
	public void setPositionCorrection(final boolean flag) { m_positionCorrection = flag; }

	/** Enable/disable continuous physics. For testing. */
	public void setContinuousPhysics(final boolean flag) { m_continuousPhysics = flag; }

	/** Perform validation of internal data structures. */
	public void validate() {
		m_broadPhase.validate();
	}

	/** Get the number of broad-phase proxies. */
	public int getProxyCount() {
		return m_broadPhase.m_proxyCount;
	}

	/** Get the number of broad-phase pairs. */
	public int getPairCount() {
		return m_broadPhase.m_pairManager.m_pairCount;
	}

	/** Get the world bounding box. */
	public AABB getWorldAABB() {
		return m_broadPhase.m_worldAABB;
	}

	/** Return true if the bounding box is within range of the world AABB. */
	public boolean inRange(final AABB aabb) {
		return m_broadPhase.inRange(aabb);
	}
	
	Segment m_raycastSegment;
	Vec2 m_raycastNormal;
	Object m_raycastUserData;
	boolean m_raycastSolidShape;
	
	/** 
	 * Query the world for all fixtures that intersect a given segment. You provide a shape
	 * pointer buffer of specified size. The number of shapes found is returned, and the buffer
	 * is filled in order of intersection
	 * @param segment defines the begin and end point of the ray cast, from p1 to p2.
	 * @param shapes a user allocated shape pointer array of size maxCount (or greater).
	 * @param maxCount the capacity of the shapes array
	 * @param solidShapes determines if shapes that the ray starts in are counted as hits.
	 * @param userData passed through the worlds contact filter, with method RayCollide. This can be used to filter valid shapes
	 * @return the number of shapes found
	 */
	public int raycast(Segment segment, Shape[] shapes, int maxCount, boolean solidShapes, Object userData)
	{
		m_raycastSegment = segment;
		m_raycastUserData = userData;
		m_raycastSolidShape = solidShapes;

		Object[] results = new Object[maxCount];

		int count = m_broadPhase.querySegment(segment,results,maxCount, raycastSortKey);

		for (int i = 0; i < count; ++i)
		{
			shapes[i] = (Shape)results[i];
		}

		return count;
	}

	/** 
	 * Performs a ray-cast as with {@link #raycast(Segment, Shape[], int, boolean, Object)}, finding the first intersecting shape
	 * @param segment defines the begin and end point of the ray cast, from p1 to p2
	 * @param lambda returns the hit fraction. You can use this to compute the contact point
	 * p = (1 - lambda) * segment.p1 + lambda * segment.p2.
	 * @param normal returns the normal at the contact point. If there is no intersection, the normal
	 * is not set.
	 * @param solidShapes determines if shapes that the ray starts in are counted as hits.
	 * @returns the colliding shape shape, or null if not found
	 * @see #raycast(Segment, Shape[], int, boolean, Object)
	 */
	public Shape raycastOne(Segment segment, RaycastResult result, boolean solidShapes, Object userData)
	{
		int maxCount = 1;
		Shape[] shapes = new Shape[maxCount];

		int count = raycast(segment, shapes, maxCount, solidShapes, userData);

		if(count==0)
			return null;

		assert(count==1);

		//Redundantly do TestSegment a second time, as the previous one's results are inaccessible
//		System.out.println("Before final test, testing shape  " + shapes[0].getType());
//		System.out.println(Arrays.toString(shapes));
		shapes[0].testSegment(shapes[0].getBody().getMemberXForm(),result,segment,1.0f);
//		System.out.println("Got here, lambda = " + result.lambda);
		//We already know it returns true
		return shapes[0];
	}
	
	private SortKeyFunc raycastSortKey = new SortKeyFunc() {
		public float apply(Object shape) {
			return raycastSortKeyFunc(shape);
		}
	};
	
	private float raycastSortKeyFunc(Object data)
	{
		Shape shape = (Shape)data;
		Body body = shape.getBody();
		World world = body.getWorld();

		if (world.m_contactFilter!=null && !world.m_contactFilter.rayCollide(world.m_raycastUserData,shape))
		{
			return -1;
		}

		RaycastResult result = new RaycastResult();
		SegmentCollide collide = shape.testSegment(body.getMemberXForm(),result, world.m_raycastSegment, 1.0f);
				//&lambda, &world->m_raycastNormal, *world->m_raycastSegment, 1);
		float lambda = result.lambda;
		
		if (world.m_raycastSolidShape && collide == SegmentCollide.MISS_COLLIDE)
		{
			return -1;
		}

		if (!world.m_raycastSolidShape && collide != SegmentCollide.HIT_COLLIDE)
		{
			return -1;
		}

		return lambda;
	}
}
