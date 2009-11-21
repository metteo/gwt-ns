package gwt.ns.gwtbox2d.client.pooling;

import gwt.ns.gwtbox2d.client.collision.Distance;
import gwt.ns.gwtbox2d.client.collision.shapes.CollideCircle;
import gwt.ns.gwtbox2d.client.collision.shapes.CollidePoly;

public final class SingletonPool {

	private static final class Singletons{
		public final CollideCircle collideCircle = new CollideCircle();
		public final CollidePoly collidePoly = new CollidePoly();
		public final Distance distance = new Distance();
	}
	
	//XXX change for gwt
	//private static final class Pool extends ThreadLocal<Singletons>{
	private static final class Pool extends notThreadLocal<Singletons>{
		protected Singletons initialValue(){
			return new Singletons();
		}
	}
	
	private static final Pool pool = new Pool();
	
	public static final CollideCircle getCollideCircle(){
		return pool.get().collideCircle;
	}
	
	public static final CollidePoly getCollidePoly(){
		return pool.get().collidePoly;
	}
	
	public static final Distance getDistance(){
		return pool.get().distance;
	}
}
