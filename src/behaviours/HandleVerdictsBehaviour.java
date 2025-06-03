package behaviours;

import java.util.Set;

import agents.TownieAgent;
import helpers.AgentFinder;
import helpers.Helpers;
import jade.core.AID;

import jade.core.behaviours.OneShotBehaviour;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class HandleVerdictsBehaviour extends OneShotBehaviour	{

	private static final long serialVersionUID = 1266715896459527283L;
	private AID accusedAID;
	private Set<AID> judgesAIDs;
	private int receivedResponses;
	private int guiltyVotes;
	private int notGuiltyVotes;
    final jade.core.Agent currentAgent = myAgent;
	public HandleVerdictsBehaviour(AID accusedAID,  Set<AID> judgesAIDs) {
		super();
		this.accusedAID = accusedAID;
		this.judgesAIDs = judgesAIDs;
	}
	@Override
	public void action() {
		   MessageTemplate mt = MessageTemplate.and(
		            MessageTemplate.MatchConversationId("verdict"),
		            MessageTemplate.or(
		                    MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
		                    MessageTemplate.MatchPerformative(ACLMessage.DISCONFIRM)
		                )
		        );
		        
		        while (receivedResponses != judgesAIDs.size() ) {
		        	
		        	
	   			ACLMessage reply = myAgent.receive(mt);
		        		        
		        if(reply != null ) {
		            receivedResponses++;
		            
		        	if(reply.getPerformative() ==  ACLMessage.CONFIRM){
		                guiltyVotes++;
		        	}else if (reply.getPerformative() ==  ACLMessage.DISCONFIRM) {
		                notGuiltyVotes++;
		        	}
		        }
		        	if (receivedResponses == judgesAIDs.size()) {
		        	    String accusedAgentName = accusedAID.getLocalName();

		    	        if (guiltyVotes > notGuiltyVotes) {
		    	        	Set<AID> recipientAIDs = AgentFinder.findAgentsAIDsByTypeExcludingSelfAndTarget(
		    	        			myAgent, "townie", accusedAgentName);
		    	        	sendRelationshipUpdate("REMOVE", recipientAIDs, accusedAgentName );       
//		    	        	   System.out.println(((TownieAgent) myAgent).getRelationships().get(accusedAgentName));
	    	        		((TownieAgent) myAgent).removeFromRelationships(accusedAgentName);
//		    	        	   System.out.println(((TownieAgent) myAgent).getRelationships().get(accusedAgentName));

	    	        	    int towniesExceptTargetAIDsSize = recipientAIDs.size();
	    	        	    myAgent.addBehaviour(new ExecutionWaitBehaviour(accusedAgentName, accusedAID, towniesExceptTargetAIDsSize));
		    	            done();
		    	        } else {
		    	            System.out.println(accusedAID.getLocalName() + " has been found not guilty");
		    	            Helpers.accusationInProgress = false;

		    	        }
	        		}

		        }
    
	}
    
    private void  sendRelationshipUpdate(String action, Set<AID> recipientAIDs, String targetAgentName) {
		
	
		
		ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
		acl.setConversationId("updateRelationshipNotification" );
		
		for (AID recipientID : recipientAIDs) {
		acl.addReceiver(recipientID);
		}
		
		String content = action + ":" + targetAgentName;
		acl.setContent(content);
		myAgent.send(acl);
}


             
            
 } 


