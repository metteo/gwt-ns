package gwt.ns.gwtbox2d.client.pooling;

import gwt.ns.gwtbox2d.client.collision.Manifold;

//XXX change for gwt
public class TLManifold extends notThreadLocal<Manifold> {
	protected Manifold initialValue(){
		return new Manifold();
	}
}

//public class TLManifold extends ThreadLocal<Manifold> {
//	protected Manifold initialValue(){
//		return new Manifold();
//	}
//}
