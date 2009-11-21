package gwt.ns.gwtbox2d.client.pooling;

import gwt.ns.gwtbox2d.client.common.Mat22;

//XXX change for gwt
public class TLMat22 extends notThreadLocal<Mat22> {
	protected Mat22 initialValue() {
		return new Mat22();
	}
}

//public class TLMat22 extends ThreadLocal<Mat22> {
//	protected Mat22 initialValue() {
//		return new Mat22();
//	}
//}