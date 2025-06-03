package behaviours;

import agents.TownieAgent;
import helpers.AgentFinder;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AccusationListeningBehavior extends CyclicBehaviour {
    
	private static final long serialVersionUID = -2825541239299003734L;
	private MessageTemplate mt;
    public AccusationListeningBehavior() {
        super();
            }
    
    @Override
    public void action() {
        mt = MessageTemplate.and(
              MessageTemplate.MatchConversationId("accusation"+ System.currentTimeMillis()%10000 + "_"),
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
            
            System.out.println(myAgent.getLocalName() + " received accusation: " + 
                               accuser + " is accusing " + accused);
//            
       
            AID accuserAID = AgentFinder.findOtherAgentAIDbyName(accuser, myAgent, "townie");
        	ACLMessage reply = accusationMsg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setConversationId("accuse response" + System.currentTimeMillis()%10000 + "_");
            reply.addReceiver(accuserAID);
            
            if (accuser != null && ((TownieAgent) myAgent).getRelationships().containsKey(accuser)) {
                int relationship = ((TownieAgent) myAgent).getRelationships().get(accuser);
                if (relationship < 10) { 
                
		            reply.setContent("SUPPORT:TRUE");
					myAgent.send(reply);

//                System.out.println(myAgent.getAID().getLocalName() + 
//                                  ": da mu hvurchi glavata na toq " + accused);
                }
            } else {
	            reply.setContent("SUPPORT:FALSE");
				myAgent.send(reply);

                System.out.println(myAgent.getLocalName() + ": ne sum suglasen s teb" + accuser);

            }

          
            
        }
    }
}