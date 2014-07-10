/**
 * Desc: class to define a region of influence for a trigger. A TriggerRegion
 * has one method, isTouching, which returns true if a given position is inside
 * the region
 *
 * @author Petr (http://www.sallyx.org/)
 */
package common.Triggers;

import common.D2.Vector2D;

abstract class TriggerRegion {

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    /**
     * returns true if an entity of the given size and position is intersecting
     * the trigger region.
     */
    abstract boolean isTouching(Vector2D EntityPos, double EntityRadius);
}
