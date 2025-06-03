package behaviours;

import agents.TownieAgent;
import helpers.Helpers;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class UpdateRelationshipsBehaviour extends CyclicBehaviour {
    
    
    private static final long serialVersionUID = 651551260163876466L;

	public UpdateRelationshipsBehaviour() {
        super();
    }
    
    @Override
    public void action() {
                MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchConversationId("updateRelationshipNotification" ),
            MessageTemplate.MatchPerformative(ACLMessage.INFORM)
        );
        
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            String content = msg.getContent();

            if (content.startsWith("REMOVE:")) {
                String executedAgentName = content.substring("REMOVE:".length());
               ((TownieAgent) myAgent).removeFromRelationships(executedAgentName);
//               System.out.println(myAgent.getLocalName() + " " + executedAgentName);
//               System.out.println(myAgent.getLocalName() + ": " + ((TownieAgent) myAgent).getRelationships());
               Helpers.agentsWhoDeletedExecutedCounter = Helpers.agentsWhoDeletedExecutedCounter + 1;
        } else if (content.startsWith("ADD:")){
            String executedAgentName = content.substring("ADD:".length());
            ((TownieAgent) myAgent).removeFromRelationships(executedAgentName);
            
        } else {
            block(1000);
        }
    }
}
}