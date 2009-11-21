package gwt.ns.gwtbox2d.client.pooling;

import gwt.ns.gwtbox2d.client.collision.AABB;

public class TLAABB extends notThreadLocal<AABB> {
	protected AABB initialValue(){
		return new AABB();
	}
}

//public class TLAABB extends ThreadLocal<AABB> {
//	protected AABB initialValue(){
//		return new AABB();
//	}
//}
