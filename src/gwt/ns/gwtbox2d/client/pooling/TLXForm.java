package gwt.ns.gwtbox2d.client.pooling;

import gwt.ns.gwtbox2d.client.common.XForm;

//XXX change for gwt
public class TLXForm extends notThreadLocal<XForm> {
	protected XForm initialValue(){
		return new XForm();
	}
}

//public class TLXForm extends ThreadLocal<XForm> {
//	protected XForm initialValue(){
//		return new XForm();
//	}
//}
