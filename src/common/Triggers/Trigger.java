/*
 *  Desc:   base class for a trigger. A trigger is an object that is
 *          activated when an entity moves within its region of influence.
 * 
 *  @author Petr (http://www.sallyx.org/)
 */
package common.Triggers;

import Raven.BaseGameEntity;
import common.D2.Vector2D;
import common.Graph.ExtraInfo;

abstract public class Trigger<entity_type extends BaseGameEntity> extends BaseGameEntity implements ExtraInfo {

    /**
     * Every trigger owns a trigger region. If an entity comes within this
     * region the trigger is activated
     */
    private TriggerRegion m_pRegionOfInfluence;
    /**
     * if this is true the trigger will be removed from the game
     */
    private boolean m_bRemoveFromGame;
    /**
     * it's convenient to be able to deactivate certain types of triggers on an
     * event. Therefore a trigger can only be triggered when this value is true
     * (respawning triggers make good use of this facility)
     */
    private boolean m_bActive;
    /**
     * some types of trigger are twinned with a graph node. This enables the
     * pathfinding component of an AI to search a navgraph for a specific type
     * of trigger.
     */
    private int m_iGraphNodeIndex;

    protected void SetGraphNodeIndex(int idx) {
        m_iGraphNodeIndex = idx;
    }

    protected void SetToBeRemovedFromGame() {
        m_bRemoveFromGame = true;
    }

    protected void SetInactive() {
        m_bActive = false;
    }

    protected void SetActive() {
        m_bActive = true;
    }

    /**
     * returns true if the entity given by a position and bounding radius is
     * overlapping the trigger region
     */
    protected boolean isTouchingTrigger(Vector2D EntityPos, double EntityRadius) {
        if (m_pRegionOfInfluence != null) {
            return m_pRegionOfInfluence.isTouching(EntityPos, EntityRadius);
        }

        return false;
    }

    //child classes use one of these methods to initialize the trigger region
    protected void AddCircularTriggerRegion(Vector2D center, double radius) {
        //if this replaces an existing region, tidy up memory
        if (m_pRegionOfInfluence != null) {
            m_pRegionOfInfluence = null;
        }

        m_pRegionOfInfluence = new TriggerRegion_Circle(center, radius);
    }

    protected void AddRectangularTriggerRegion(Vector2D TopLeft, Vector2D BottomRight) {
        //if this replaces an existing region, tidy up memory
        if (m_pRegionOfInfluence == null) {
            m_pRegionOfInfluence = null;
        }
        m_pRegionOfInfluence = new TriggerRegion_Rectangle(TopLeft, BottomRight);
    }

    public Trigger(int id) {
        super(id);
        m_bRemoveFromGame = false;
        m_bActive = true;
        m_iGraphNodeIndex = -1;
        m_pRegionOfInfluence = null;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        m_pRegionOfInfluence = null;
    }

    /**
     * when this is called the trigger determines if the entity is within the
     * trigger's region of influence. If it is then the trigger will be
     * triggered and the appropriate action will be taken.
     */
    public abstract void Try(entity_type entity);

    /**
     * called each update-step of the game. This methods updates any internal
     * state the trigger may have
     */
    @Override
    public abstract void Update();

    public int GraphNodeIndex() {
        return m_iGraphNodeIndex;
    }

    public boolean isToBeRemoved() {
        return m_bRemoveFromGame;
    }

    public boolean isActive() {
        return m_bActive;
    }
}