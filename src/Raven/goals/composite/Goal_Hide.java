/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Raven.goals.composite;

import Raven.Raven_Bot;
import static Raven.goals.Raven_Goal_Types.goal_hide;
import common.D2.Vector2D;
import static Raven.goals.atomic.Goal.failed;
import Raven.goals.atomic.Goal_SeekToPosition;
import common.Messaging.Telegram;

/**
 *
 * @author Artlisflame
 */
public class Goal_Hide extends Goal_Composite<Raven_Bot>{
    
    private Vector2D m_CurrentDestination = new Vector2D();
    private int m_SearchAttempts;
    private final int m_MaxAttempts = 10;
    
    public Goal_Hide (Raven_Bot pBot) {
        super(pBot, goal_hide);
    }
    
//the usual suspects
    @Override
    public void Activate() {
        m_iStatus = active;

        //if this goal is reactivated then there may be some existing subgoals that
        //must be removed
        RemoveAllSubgoals();
        
        if (m_pOwner.GetTargetSys().isTargetPresent()) {
            //search for a spot that has no LOS to known enemies
            m_SearchAttempts = 0;
            double time;
            while (m_SearchAttempts < m_MaxAttempts){
                m_SearchAttempts += 1;

                m_CurrentDestination = m_pOwner.GetWorld().GetMap().GetRandomNodeLocation();
                time = m_pOwner.CalculateTimeToReachPosition(m_CurrentDestination);
                if (time < m_pOwner.GetTargetBot().CalculateTimeToReachPosition(m_CurrentDestination) && time < 2){ 
                    if (!m_pOwner.GetTargetBot().hasLOSto(m_CurrentDestination)){
                        m_pOwner.GetPathPlanner().RequestPathToPosition(m_CurrentDestination);
                        m_SearchAttempts = m_MaxAttempts + 1;
                    }
                }
            }
        }
        if(m_SearchAttempts > m_MaxAttempts) {
            AddSubgoal(new Goal_SeekToPosition(m_pOwner, m_CurrentDestination));
        } else {
            m_iStatus = failed;
        }           
    }

    @Override
    public int Process() {
        //if status is inactive, call Activate()
        ActivateIfInactive();

        m_iStatus = ProcessSubgoals();
        
        if (!m_pOwner.GetTargetSys().isTargetShootable()) {
            m_iStatus = completed;
        }

        return m_iStatus;
    }

    @Override
    public void Terminate() {
		RemoveAllSubgoals();
    }

    @Override
    public boolean HandleMessage(final Telegram msg) {
        //first, pass the message down the goal hierarchy
        boolean bHandled = ForwardMessageToFrontMostSubgoal(msg);

        //if the msg was not handled, test to see if this goal can handle it
        if (bHandled == false) {
            switch (msg.Msg) {
                case Msg_PathReady:

                    //clear any existing goals
                    RemoveAllSubgoals();

                    AddSubgoal(new Goal_FollowPath(m_pOwner,
                            m_pOwner.GetPathPlanner().GetPath()));

                    return true; //msg handled


                case Msg_NoPathAvailable:

                    m_iStatus = failed;

                    return true; //msg handled

                default:
                    return false;
            }
        }

        //handled by subgoals
        return true;
    }
    
    /*@Override
    public void Render() {
    
    }*/    
}
