package gwt.ns.gwtbox2d.client.pooling;

import gwt.ns.gwtbox2d.client.collision.BoundValues;

//XXX change for gwt
public class TLBoundValues extends notThreadLocal<BoundValues> {
	protected BoundValues initialValue(){
		return new BoundValues();
	}
}

//public class TLBoundValues extends ThreadLocal<BoundValues> {
//	protected BoundValues initialValue(){
//		return new BoundValues();
//	}
//}
