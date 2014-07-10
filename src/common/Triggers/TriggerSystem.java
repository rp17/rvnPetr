/**
 * Desc: Class to manage a collection of triggers. Triggers may be registered
 * with an instance of this class. The instance then takes care of updating
 * those triggers and of removing them from the system if their lifetime has
 * expired.
 *
 * @author Petr (http://www.sallyx.org/)
 */
package common.Triggers;

import Raven.Raven_Bot;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TriggerSystem<trigger_type extends Trigger> {

    public class TriggerList extends ArrayList<trigger_type> {
    };
    private TriggerList m_Triggers = new TriggerList();

    /**
     * this method iterates through all the triggers present in the system and
     * calls their Update method in order that their internal state can be
     * updated if necessary. It also removes any triggers from the system that
     * have their m_bRemoveFromGame field set to true.
     */
    private void UpdateTriggers() {
        Iterator<trigger_type> it = m_Triggers.iterator();
        while (it.hasNext()) {
            trigger_type curTrg = it.next();
            if (curTrg.isToBeRemoved()) {
                it.remove();
            } else {
                curTrg.Update();
            }
        }
    }

    /**
     * this method iterates through the container of entities passed as a
     * parameter and passes each one to the Try method of each trigger
     * *provided* the entity is alive and provided the entity is ready for a
     * trigger update.
     */
    private <ContainerOfEntities extends List<? extends Raven_Bot>> void TryTriggers(ContainerOfEntities entities) {
        //test each entity against the triggers
        for (Raven_Bot curEnt : entities) {
            //an entity must be ready for its next trigger update and it must be 
            //alive before it is tested against each trigger.
            if (curEnt.isReadyForTriggerUpdate() && curEnt.isAlive()) {
                for (Trigger curTrg : m_Triggers) {
                    curTrg.Try(curEnt);
                }
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Clear();
    }

    /**
     * this deletes any current triggers and empties the trigger list
     */
    public void Clear() {
        m_Triggers.clear();
    }

    /**
     * This method should be called each update-step of the game. It will first
     * update the internal state odf the triggers and then try each entity
     * against each active trigger to test if any should be triggered.
     */
    public <ContainerOfEntities extends List<? extends Raven_Bot>> void Update(ContainerOfEntities entities) {
        UpdateTriggers();
        TryTriggers(entities);
    }

    /**
     * this is used to register triggers with the TriggerSystem (the
     * TriggerSystem will take care of tidying up memory used by a trigger)
     */
    public void Register(trigger_type trigger) {
        m_Triggers.add(trigger);
    }

    /**
     * some triggers are required to be rendered (like giver-triggers for
     * example)
     */
    public void Render() {
        for (trigger_type curTrg : m_Triggers) {
            curTrg.Render();
        }
    }

    public TriggerList GetTriggers() {
        return m_Triggers;
    }
}