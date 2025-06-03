package behaviours;

import java.util.ArrayList;
import java.util.Set;

import agents.TownieAgent;
import helpers.AgentFinder;
import helpers.Helpers;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CollectAccusationSupportBehaviour extends Behaviour {
    private static final long serialVersionUID = 6290284283747793616L;
	private AID accusedAgentAID;
    private ArrayList<AID> towniesExceptTarget;
    private int receivedResponses = 0;
    private int supportVotes = 0;
    private final int EXECUTION_THRESHOLD = 1;
    boolean trialHeld;

    
    public CollectAccusationSupportBehaviour(TownieAgent agent, AID accusedAgent, 
                                          ArrayList<AID> towniesExceptTarget) {
        super(agent);
        this.accusedAgentAID = accusedAgent;
        this.towniesExceptTarget = towniesExceptTarget;
    }
    
    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchConversationId("accuse response" + + System.currentTimeMillis()%10000 + "_") ,
            MessageTemplate.MatchPerformative(ACLMessage.INFORM)
        );
        
        ACLMessage reply = myAgent.receive(mt);
        
        if (reply != null) {
            receivedResponses++;
            
            String content = reply.getContent();
            if (content.equals("SUPPORT:TRUE")) {
                supportVotes++;
         	   System.out.println(reply.getSender().getLocalName() + 
                     " supports the accusation against " + 
                     accusedAgentAID.getLocalName());
         	  
             
            } else {
                System.out.println(reply.getSender().getLocalName() + 
                                  " does not support the accusation against " + 
                                  accusedAgentAID.getLocalName());


            }
            
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
        	Helpers.accusationInProgress = false;
        }
        return isDone;
    }
    
 
    
	 public  void holdTrial(Agent myAgent, AID accusedAID) {
			ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);
			Set<AID> judgesAIDs = AgentFinder.findAgentAIDsByType(myAgent, "judge");
			for(AID judgeAID: judgesAIDs) {
				acl.addReceiver(judgeAID);
	    	}
		
	        acl.setConversationId("deliberate" + System.currentTimeMillis()%10000 + "_");
	        acl.setContent("DELIBERATE::" + accusedAID.getLocalName() + "::" + myAgent.getLocalName());
	       	myAgent.send(acl);
	       myAgent.addBehaviour(new HandleVerdictsBehaviour(accusedAID, judgesAIDs));
	    }
   
}