package gwt.ns.gwtbox2d.client.pooling;

import gwt.ns.gwtbox2d.client.dynamics.TimeStep;

//XXX change for gwt
public class TLTimeStep extends notThreadLocal<TimeStep> {
	@Override
	protected TimeStep initialValue(){
		return new TimeStep();
	}
}

//public class TLTimeStep extends ThreadLocal<TimeStep> {
//	@Override
//	protected TimeStep initialValue(){
//		return new TimeStep();
//	}
//}
