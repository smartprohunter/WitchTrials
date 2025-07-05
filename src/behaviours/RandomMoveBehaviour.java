package behaviours;

import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import ui.GridManager;

import java.util.ArrayList;

import agents.TownieAgent;
import helpers.AgentRegistry;
import helpers.Helpers;


public class RandomMoveBehaviour extends TickerBehaviour {
    
   
	private static final long serialVersionUID = 1L;

	public RandomMoveBehaviour(TownieAgent agent, long period) {
        super(agent, period);
    }
    
    @Override
    protected void onTick() {
    	  if (!Helpers.restartTriggered && AgentRegistry.findAgentAIDsByType(myAgent, "townie").size() <= 2) {
    		  Helpers.restartTriggered = true;
              System.out.println("Only 2 townies left. Restarting simulation...");
              Helpers.shutDownSimulation();
            }
    	
        GridManager gridManager = GridManager.getInstance();
        
        gridManager.moveAgentRandomly(myAgent.getLocalName(), ((TownieAgent) myAgent).getGender());
        
        ArrayList<AID> samePositionTownies = AgentRegistry.findTowniesAIDAtSamePosition(myAgent);
        if (!samePositionTownies.isEmpty()) {
        	myAgent.addBehaviour(new TownieActivitiesBehaviour((TownieAgent) myAgent, samePositionTownies));
        }
        
       
    }
}
