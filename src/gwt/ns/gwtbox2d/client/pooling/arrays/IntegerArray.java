package gwt.ns.gwtbox2d.client.pooling.arrays;

public class IntegerArray extends DynamicTLArray<Integer> {
	@Override
	protected final Integer[] getInitializedArray(int argLength) {
		return new Integer[argLength];
	}
}
