package gwt.ns.gwtbox2d.client.pooling.arrays;

import gwt.ns.gwtbox2d.client.common.Vec2;

public class Vec2Array extends DynamicTLArray<Vec2> {

	@Override
	protected Vec2[] getInitializedArray(int argLength) {
		Vec2[] ray = new Vec2[argLength];
		for(int i=0; i<ray.length; i++){
			ray[i] = new Vec2();
		}
		return ray;
	}

}
