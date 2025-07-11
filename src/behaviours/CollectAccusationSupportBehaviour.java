package behaviours;

import java.util.ArrayList;
import java.util.Set;

import agents.TownieAgent;
import helpers.AgentRegistry;
import helpers.Helpers;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import ui.GridManager;

public class CollectAccusationSupportBehaviour extends Behaviour {
    private static final long serialVersionUID = 6290284283747793616L;
	private AID accusedAgentAID;
    private ArrayList<AID> towniesExceptTarget;
    private int receivedResponses = 0;
    private int supportVotes = 0;
    private final int EXECUTION_THRESHOLD = 1;
    boolean trialHeld;
    GridManager instance = GridManager.getInstance();
    
    public CollectAccusationSupportBehaviour(TownieAgent agent, AID accusedAgent, 
                                          ArrayList<AID> towniesExceptTarget) {
        super(agent);
        this.accusedAgentAID = accusedAgent;
        this.towniesExceptTarget = towniesExceptTarget;
    }
    
    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchConversationId("accuse response") ,
            MessageTemplate.MatchPerformative(ACLMessage.INFORM)
        );
        
        ACLMessage reply = myAgent.receive(mt);
        
        if (reply != null) {
            receivedResponses++;
            
            String content = reply.getContent();
            if (content.equals("SUPPORT:TRUE")) {
                supportVotes++;
                instance.logToUI(reply.getSender().getLocalName() + 
                     " supports the accusation against " + 
                     accusedAgentAID.getLocalName());
         	  
             
            } else {
            	instance.logToUI(reply.getSender().getLocalName() + 
                                  " does not support the accusation against " + 
                                  accusedAgentAID.getLocalName());


            }
            
     }  else {
    	 block();
     }
    }
    
    @Override
    public boolean done() {
    	
    	 if(supportVotes >= EXECUTION_THRESHOLD) {
    		  trialHeld = true;
    	      holdTrial(myAgent, accusedAgentAID);
    	   }
        boolean isDone =  receivedResponses >= towniesExceptTarget.size() || trialHeld;
        if(!trialHeld && isDone) {
        	System.out.println("lock dropped collect accusation");
        	Helpers.releaseLock(myAgent.getLocalName());
        }
        return isDone;
    }
    
 
    
	 public  void holdTrial(Agent myAgent, AID accusedAID) {
			ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);
			Set<AID> judgesAIDs = AgentRegistry.findAgentAIDsByType(myAgent, "judge");
			for(AID judgeAID: judgesAIDs) {
				acl.addReceiver(judgeAID);
	    	}
		
	        acl.setConversationId("deliberate" );
	        acl.setContent("DELIBERATE::" + accusedAID.getLocalName() + "::" + myAgent.getLocalName());
	       	myAgent.send(acl);
	       myAgent.addBehaviour(new HandleVerdictsBehaviour(accusedAID, judgesAIDs));
	    }
   
}