package behaviours;

import jade.core.behaviours.TickerBehaviour;
import ui.GridManager;

import agents.TownieAgent;


public class RandomMoveBehaviour extends TickerBehaviour {
    
   
	private static final long serialVersionUID = 1L;

	public RandomMoveBehaviour(TownieAgent agent, long period) {
        super(agent, period);
    }
    
    @Override
    protected void onTick() {
        GridManager gridManager = GridManager.getInstance();
        
        gridManager.moveAgentRandomly(myAgent.getLocalName());
        
//        if (newPosition != null) {
//            System.out.println(myAgent.getLocalName() + " moved to position (" + 
//                              newPosition.x + ", " + newPosition.y + ")");
//        }
    }
}
