package gwt.ns.gwtbox2d.client.pooling.stacks;

import gwt.ns.gwtbox2d.client.dynamics.TimeStep;

public class TimeStepStack extends DynamicTLStack<TimeStep> {
	@Override
	protected TimeStep newObjectInstance() {
		return new TimeStep();
	}
}
