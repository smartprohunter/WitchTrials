package behaviours;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import agents.TownieAgent;
import helpers.AgentFinder;
import helpers.Helpers;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class TownieActivitiesBehaviour extends CyclicBehaviour {
    private static final long serialVersionUID = 5738291047385621908L;
    private TownieAgent agent;
    private Random random = new Random();
    private static final long ACTIVITY_COOLDOWN = 100000; // 1 second cooldown between gossips
    private static String[] gossipTemplates = {
            "{subject} was seen talking to a black cat at midnight",
            "{subject} has a strange mark that could be a witch's sign",
            "{subject}'s crops never fail, even in the worst weather",
            "{subject} was muttering strange words in the forest",
            "{subject} knows remedies that no one else knows",
            "{subject} predicted the storm last month before it happened",
            "{subject} never attends church services",
            "{subject} lives alone and rarely speaks to anyone",
            "{subject} was seen drawing strange symbols outside their home",
            "{subject} has threatened revenge against those who wronged them"
    };

    public TownieActivitiesBehaviour(TownieAgent a) {
        super(a);
        agent = a;
    }

    @Override
    public void action() {
        ArrayList<AID> samePositionTownies = AgentFinder.findTowniesAIDAtSamePosition(myAgent);

        if (!samePositionTownies.isEmpty() ) {
            double activityRoll = random.nextDouble(); // sled nqkolko ekzekucii vs spira
       	 synchronized (Helpers.class) {
       	  if (!Helpers.accusationInProgress) {
            if (activityRoll < 0.7) { 
                performGossip(samePositionTownies);
            } else { 
            	considerAccusing(samePositionTownies);
                     }
                 }
            }
            block(ACTIVITY_COOLDOWN); 

        }
        
    }
    
    private void performGossip(ArrayList<AID> samePositionTownies) {
        

            // Choose a random townie from the same position to gossip with
            int randomIndex = random.nextInt(samePositionTownies.size());
            AID targetTownieAID = samePositionTownies.get(randomIndex);
            
            Set<AID> potentialGossipSubjects = AgentFinder.findAgentsAIDsByTypeExcludingSelfAndTarget(
                    myAgent, "townie", targetTownieAID.getLocalName());
            
            if (!potentialGossipSubjects.isEmpty()) {
                gossipWith(targetTownieAID, potentialGossipSubjects);
            }
        
    }
    
    private void gossipWith(AID receiverAID, Set<AID> potentialGossipSubjects) {
               
        List<AID> subjectsList = new ArrayList<>(potentialGossipSubjects);
        int randomIndex = random.nextInt(subjectsList.size());
        AID randomAID = subjectsList.get(randomIndex);
        String gossipTargetName = randomAID.getLocalName();
        String gossipContent = getRandomGossip(gossipTargetName); 
        
        ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
        acl.addReceiver(receiverAID);
        acl.setContent("GOSSIP::" + gossipTargetName + "::" + gossipContent);
        acl.setConversationId("gossip" + System.currentTimeMillis() % 10000 + "_");
        
        agent.send(acl);
//        System.out.println(myAgent.getLocalName() + ": Hey, " + 
//                receiverAID.getLocalName() + ", " + gossipContent);
    }
    
    private String getRandomGossip(String subject) {
        int index = random.nextInt(gossipTemplates.length);
        String template = gossipTemplates[index];
        return template.replace("{subject}", subject);
    }
    
   
    private void considerAccusing(ArrayList<AID> samePositionTownies) {
    	if (AgentFinder.findAgentAIDsByType(agent, "townie").size() <=2) {
    		System.out.println("No more townies to accuse.");
    		return;
    	}
        String targetTownieName = agent.getLeastLikedTownie();
//        System.out.println(targetTownieName);
        Map<String, Integer> agentRelationships = ((TownieAgent) myAgent).getRelationships();
        AID targetAID = AgentFinder.findOtherAgentAIDbyName(targetTownieName, myAgent, "townie");
        Integer targetRelationship = agentRelationships.getOrDefault(targetTownieName, 0);
        if (targetRelationship < 10 && !samePositionTownies.contains(targetAID) ) {
        	Helpers.accusationInProgress = true;
//            System.out.println(targetAID);

            ACLMessage acl = new ACLMessage(ACLMessage.PROPOSE);
            
            for (AID townieID : samePositionTownies) {
                acl.addReceiver(townieID);
            }
            
            String accusationContent = "ACCUSATION:" + myAgent.getLocalName() + ":" + targetTownieName;
            acl.setContent(accusationContent);
            String conversationId = "accusation" + System.currentTimeMillis() % 10000 + "_";
            acl.setConversationId(conversationId);
            
            myAgent.send(acl);
            System.out.println(myAgent.getAID().getLocalName() + ": " + targetTownieName + " e veshtica.");
            
            agent.addBehaviour(new CollectAccusationSupportBehaviour(agent, targetAID, samePositionTownies));
        }
    }
}