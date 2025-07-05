package behaviours;

import agents.TownieAgent;
import helpers.AgentRegistry;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import ui.GridManager;

public class AccusationListeningBehavior extends CyclicBehaviour {
    
	private static final long serialVersionUID = -2825541239299003734L;
	private MessageTemplate mt;
    GridManager instance = GridManager.getInstance();

    public AccusationListeningBehavior() {
        super();
            }
    
    @Override
    public void action() {
        mt = MessageTemplate.and(
              MessageTemplate.MatchConversationId("accusation"),
              MessageTemplate.MatchPerformative(ACLMessage.PROPOSE)
              ); 
        
        ACLMessage acl = myAgent.receive(mt);
        
        if (acl != null) {
            String content = acl.getContent();
            
 
            if (content.startsWith("ACCUSATION:")) {
                handleAccusation(acl,content);
            }
        }
    }
    
    private void handleAccusation(ACLMessage accusationMsg, String content) {
        String[] parts = content.split(":");
        
        if (parts.length >= 3) {
            String accuser = parts[1];
            String accused = parts[2];
            
            instance.logToUI(myAgent.getLocalName() + " received accusation: " + 
                               accuser + " is accusing " + accused);
       
            AID accuserAID = AgentRegistry.findOtherTownieAIDbyName(accused, myAgent);
        	ACLMessage reply = accusationMsg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setConversationId("accuse response" );
            reply.addReceiver(accuserAID);

                int relationship = ((TownieAgent) myAgent).getRelationships().get(accused);
                if (relationship < 10) { 
                
		            reply.setContent("SUPPORT:TRUE");
					myAgent.send(reply);

                }
             else {
	            reply.setContent("SUPPORT:FALSE");
				myAgent.send(reply);

				instance.logToUI(myAgent.getLocalName() + ": I don't aggree that they are a witch," + accuser +".");

            }

          
            
        }
    }
}