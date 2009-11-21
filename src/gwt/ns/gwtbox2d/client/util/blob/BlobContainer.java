package gwt.ns.gwtbox2d.client.util.blob;

import gwt.ns.gwtbox2d.client.collision.AABB;
import gwt.ns.gwtbox2d.client.common.Vec2;

public interface BlobContainer {
	/**
	 * Is the Vec2 within the desired geometry?
	 * @param p The point to test
	 * @return True if the geometry contains the point
	 */
	public boolean containsPoint(Vec2 p);
	
	/** Get the world AABB of the container. */
	public AABB getAABB();
}
