package gwt.ns.gwtbox2d.client.util.blob;

import gwt.ns.gwtbox2d.client.common.Vec2;

public class BlobPoint {
	public Vec2 position;
	public float mass = 1.0f;
	
	public BlobPoint(float x, float y) {
		position = new Vec2(x,y);
	}
}
