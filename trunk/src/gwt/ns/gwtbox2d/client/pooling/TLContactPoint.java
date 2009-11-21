package gwt.ns.gwtbox2d.client.pooling;

import gwt.ns.gwtbox2d.client.dynamics.contacts.ContactPoint;

//XXX change for gwt
public class TLContactPoint extends notThreadLocal<ContactPoint> {
	protected ContactPoint initialValue(){
		return new ContactPoint();
	}
}

//public class TLContactPoint extends ThreadLocal<ContactPoint> {
//	protected ContactPoint initialValue(){
//		return new ContactPoint();
//	}
//}