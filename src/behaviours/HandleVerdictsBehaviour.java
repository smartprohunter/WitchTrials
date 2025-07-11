package behaviours;

import java.util.Set;

import agents.TownieAgent;
import helpers.AgentRegistry;
import helpers.Helpers;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import ui.GridManager;

public class HandleVerdictsBehaviour extends CyclicBehaviour {

    private static final long serialVersionUID = 1266715896459527283L;

    private final AID accusedAID;
    private final Set<AID> judgesAIDs;
    private int receivedResponses = 0;
    private int guiltyVotes = 0;
    private int notGuiltyVotes = 0;

    private final GridManager instance = GridManager.getInstance();

    private final MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchConversationId("verdict"),
            MessageTemplate.or(
                    MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
                    MessageTemplate.MatchPerformative(ACLMessage.DISCONFIRM)
            )
    );

    public HandleVerdictsBehaviour(AID accusedAID, Set<AID> judgesAIDs) {
        this.accusedAID = accusedAID;
        this.judgesAIDs = judgesAIDs;
    }

    @Override
    public void action() {
        ACLMessage reply = myAgent.receive(mt);

        if (reply != null) {
            receivedResponses++;

            if (reply.getPerformative() == ACLMessage.CONFIRM) {
                guiltyVotes++;
            } else if (reply.getPerformative() == ACLMessage.DISCONFIRM) {
                notGuiltyVotes++;
            }

            // All votes received, process verdict
            if (receivedResponses >= judgesAIDs.size()) {
                processVerdict();
//                getAgent().removeBehaviour(this);
            }

        } 
    }

    private void processVerdict() {
        String accusedAgentName = accusedAID.getLocalName();
        System.out.println("Processing verdict for: " + accusedAgentName);
        System.out.println("Votes - Guilty: " + guiltyVotes + ", Not Guilty: " + notGuiltyVotes);

        if (guiltyVotes > notGuiltyVotes) {
            Set<AID> recipientAIDs = AgentRegistry.findAgentsAIDsByTypeExcludingSelfAndTarget(
                    myAgent, "townie", accusedAgentName);
            for (AID townie : recipientAIDs) {
            	TownieAgent agent = AgentRegistry.getTownie(townie);
            	agent.removeFromRelationships(accusedAgentName);
            }           
            ((TownieAgent) myAgent).removeFromRelationships(accusedAgentName);
            getAgent().addBehaviour(new ExecutionWaitBehaviour(accusedAgentName, accusedAID, 0));

        } else {
            instance.logToUI(accusedAgentName + " has been found not guilty");
            Helpers.releaseLock(myAgent.getLocalName());
        	System.out.println("lock dropped collect verdict");

        }
    }

    private void sendRelationshipUpdate(String action, Set<AID> recipientAIDs, String targetAgentName) {
    	
//        ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
//        acl.setConversationId("updateRelationshipNotification");
//
//        for (AID recipientID : recipientAIDs) {
//            acl.addReceiver(recipientID);
//        }
//
//        String content = action + ":" + targetAgentName;
//        acl.setContent(content);
//        myAgent.send(acl);
    	
    }
}
