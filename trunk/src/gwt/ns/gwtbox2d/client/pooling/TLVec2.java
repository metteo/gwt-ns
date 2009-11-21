package gwt.ns.gwtbox2d.client.pooling;

import gwt.ns.gwtbox2d.client.common.Vec2;

//XXX change for gwt
public class TLVec2 extends notThreadLocal<Vec2> {
	protected Vec2 initialValue(){
		return new Vec2();
	}
}

//public class TLVec2 extends ThreadLocal<Vec2> {
//	protected Vec2 initialValue(){
//		return new Vec2();
//	}
//}
