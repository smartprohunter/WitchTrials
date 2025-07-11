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
import agents.TownieAgent;
import helpers.AgentRegistry;
import helpers.Helpers;

public  class ExecutionWaitBehaviour extends CyclicBehaviour {
    
    private static final long serialVersionUID = 1L;
    
    private final String executedAgentName;
    private final AID accusedAID;
    private final int requiredDeletionCount;
    GridManager instance = GridManager.getInstance();
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
        } 
    }
    
    private void completeExecution() {
        try {
        	System.out.println("executing");
			TownieAgent accusedAgent = AgentRegistry.getTownie(accusedAID);
            Helpers.hysteria += 10 * accusedAgent.getSocialStatus();
            System.out.println(Helpers.hysteria) ;

            AgentContainer container = myAgent.getContainerController();
            AgentController agentController = container.getAgent(executedAgentName);
            
            GridManager grid = GridManager.getInstance();
            grid.removeAgent(executedAgentName);
            agentController.kill();
            deregister();
            Helpers.agentsWhoDeletedExecutedCounter = 0;
            instance.logToUI(getRandomExecutionMessage(executedAgentName));
        	Helpers.releaseLock(myAgent.getLocalName());
            myAgent.removeBehaviour(this);
        } catch (ControllerException e) {
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
    
    private String getRandomExecutionMessage(String agentName) {
        String[] executions = {
            agentName + " has been hanged for witchcraft.",
            agentName + " was burned at the stake for practicing dark magic.",
            agentName + " was pressed to death with heavy stones.",
            agentName + " drowned during trial by water.",
            agentName + " was executed by hanging at Gallows Hill.",
            agentName + " perished in the flames for consorting with the devil.",
            agentName + " met their end at the executioner's block.",
            agentName + " was condemned to the flames for sorcery."
        };
        
        return executions[(int)(Math.random() * executions.length)];
    }
}