/*
 * Desc:     defines a trigger that only remains in the game for a specified
 *           number of update steps
 *  @author Petr (http://www.sallyx.org/)
 */
package common.Triggers;

import Raven.BaseGameEntity;

abstract public class Trigger_LimitedLifetime<entity_type extends BaseGameEntity> extends Trigger<entity_type> {

    /**
     * the lifetime of this trigger in update-steps
     */
    protected int m_iLifetime;

    public Trigger_LimitedLifetime(int lifetime) {
        super(BaseGameEntity.GetNextValidID());
        m_iLifetime = lifetime;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    /**
     * children of this class should always make sure this is called from within
     * their own update method
     */
    public void Update() {
        //if the lifetime counter expires set this trigger to be removed from
        //the game
        if (--m_iLifetime <= 0) {
            SetToBeRemovedFromGame();
        }
    }

    /**
     * to be implemented by child classes
     */
    @Override
    abstract public void Try(entity_type entity);
}
