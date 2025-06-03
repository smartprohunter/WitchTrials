package behaviours;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import ui.GridManager;
import helpers.AgentFinder;
import helpers.Helpers;

public class ExecutionWaitBehaviour extends CyclicBehaviour {
    
    private static final long serialVersionUID = 1L;
    
    private final String executedAgentName;
    private final AID accusedAID;
    private final int requiredDeletionCount;
    
    public ExecutionWaitBehaviour(String executedAgentName, AID accusedAID, int requiredDeletionCount) {
        super();
        this.executedAgentName = executedAgentName;
        this.accusedAID = accusedAID;
        this.requiredDeletionCount = requiredDeletionCount;
    }
    
    @Override
    public void action() {
//    	System.out.println(requiredDeletionCount);
//    	System.out.println(Helpers.agentsWhoDeletedExecutedCounter);

        if (requiredDeletionCount == Helpers.agentsWhoDeletedExecutedCounter) {
     
            completeExecution();
            myAgent.removeBehaviour(this); 
        } else {
            block(100); 
        }
    }
    
    private void completeExecution() {
        try {
            AgentContainer container = myAgent.getContainerController();
            AgentController agentController = container.getAgent(executedAgentName);
            
            GridManager grid = GridManager.getInstance();
            grid.removeAgent(executedAgentName);
            agentController.kill();
            deregister();
            AgentFinder.remakeRegistry(myAgent, true);
            Helpers.agentsWhoDeletedExecutedCounter = 0;
            
            System.out.println(executedAgentName + " pukna");
            Helpers.accusationInProgress = false;
        } catch (ControllerException e) {
            System.err.println("Error killing agent " + executedAgentName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void deregister() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(accusedAID);
        
        try {
            DFService.deregister(myAgent, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}