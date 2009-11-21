package gwt.ns.gwtbox2d.client.pooling;

import gwt.ns.gwtbox2d.client.dynamics.contacts.ContactSolver;

//XXX change for gwt
public class TLContactSolver extends notThreadLocal<ContactSolver> {
	protected ContactSolver initialValue(){
		return new ContactSolver();
	}
}

//public class TLContactSolver extends ThreadLocal<ContactSolver> {
//	protected ContactSolver initialValue(){
//		return new ContactSolver();
//	}
//}
