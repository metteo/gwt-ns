package gwt.ns.gwtbox2d.client.pooling;

import gwt.ns.gwtbox2d.client.collision.MassData;

//XXX change for gwt
public class TLMassData extends notThreadLocal<MassData> {
	protected MassData initialValue(){
		return new MassData();
	}
}

//public class TLMassData extends ThreadLocal<MassData> {
//	protected MassData initialValue(){
//		return new MassData();
//	}
//}
