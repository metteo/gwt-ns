package gwt.ns.gwtbox2d.client.collision.shapes;

import gwt.ns.gwtbox2d.client.common.Vec2;

/**
 * Point shape definition.
 */
public class PointDef extends ShapeDef {
	public Vec2 localPosition;
	public float mass;

	public PointDef() {
		type = ShapeType.POINT_SHAPE;
		localPosition = new Vec2(0.0f, 0.0f);
		mass = 0.0f;
	}
}
