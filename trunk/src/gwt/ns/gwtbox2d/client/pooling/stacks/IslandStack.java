package gwt.ns.gwtbox2d.client.pooling.stacks;

import gwt.ns.gwtbox2d.client.dynamics.Island;

public class IslandStack extends DynamicTLStack<Island> {
	@Override
	protected Island newObjectInstance() {
		return new Island();
	}
}
