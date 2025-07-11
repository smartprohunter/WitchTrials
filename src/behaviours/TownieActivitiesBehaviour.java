package behaviours;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import agents.TownieAgent;
import helpers.AgentRegistry;
import helpers.Helpers;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import ui.GridManager;

public class TownieActivitiesBehaviour extends OneShotBehaviour {
    private static final long serialVersionUID = 5738291047385621908L;
    private TownieAgent agent;
    private Random random = new Random();
    GridManager instance = GridManager.getInstance();
    ArrayList<AID> samePositionTownies;

    public TownieActivitiesBehaviour(TownieAgent a, ArrayList<AID> samePositionTownies) {
        super(a);
        agent = a;
        this.samePositionTownies = samePositionTownies;
    }

  
	@Override
    public void action() {
    	
    	  
        
        
       
          double activityRoll = random.nextDouble();
//          System.out.println("roll:" + activityRoll);
       	  if (Helpers.tryAcquireLock(myAgent.getLocalName())) {
		  
			  if ((activityRoll > 0.2 && Helpers.hysteria > 50) ||
					    (activityRoll > 0.4 && Helpers.hysteria > 25) ||
					    (activityRoll > 0.6)) {
					    considerAccusing(samePositionTownies);
					} else {
					    performGossip(samePositionTownies);
					}


        
        
    }
	}
    
    private void performGossip(ArrayList<AID> samePositionTownies) {
        

            int randomIndex = random.nextInt(samePositionTownies.size());
            AID targetTownieAID = samePositionTownies.get(randomIndex);
            
            Set<AID> potentialGossipSubjects = AgentRegistry.findAgentsAIDsByTypeExcludingSelfAndTarget(
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
        String gossipContent = getRandomGossipMessage(gossipTargetName); 
        
        ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
        acl.addReceiver(receiverAID);
        acl.setContent("GOSSIP::" + gossipTargetName + "::" + gossipContent);
        acl.setConversationId("gossip" + "_");
        
        agent.send(acl);
        instance.logToUI
        (myAgent.getLocalName() + ": Hey, " + 
                receiverAID.getLocalName() + ", " + gossipContent);
		Helpers.releaseLock(myAgent.getLocalName());;

    }
    
    private String getRandomGossipMessage(String subjectName) {
        String[] gossipTemplates = {
            subjectName + " was seen talking to a black cat at midnight",
            subjectName + " has a strange mark that could be a witch's sign", 
            subjectName + "'s crops never fail, even in the worst weather",
            subjectName + " was muttering strange words in the forest",
            subjectName + " knows remedies that no one else knows",
            subjectName + " predicted the storm last month before it happened",
            subjectName + " never attends church services",
            subjectName + " lives alone and rarely speaks to anyone",
            subjectName + " was seen drawing strange symbols outside their home",
            subjectName + " has threatened revenge against those who wronged them",
            
            subjectName + " was spotted dancing naked under the full moon",
            subjectName + " speaks to animals and they seem to understand",
            subjectName + " has eyes that glow strangely in candlelight",
            subjectName + " makes potions from mysterious herbs",
            subjectName + " was seen flying on a broomstick near the old mill",
            subjectName + " cursed farmer Johnson's cow and it died the next day",
            subjectName + " knows things about people they shouldn't know",
            subjectName + " has been seen entering the forbidden woods at night",
            subjectName + " whispers incantations while cooking",
            subjectName + " owns books written in strange languages",
            subjectName + " can predict when someone will fall ill",
            subjectName + " was found with suspicious herbs and bones",
            subjectName + " claims to speak with the spirits of the dead",
            subjectName + " has been seen collecting spider webs and bat wings"
        };
        
        return gossipTemplates[(int)(Math.random() * gossipTemplates.length)];
    }
    
   
    private void considerAccusing(ArrayList<AID> samePositionTownies) {
        String targetTownieName = agent.getLeastLikedTownie();
        Map<String, Integer> agentRelationships = ((TownieAgent) myAgent).getRelationships();
        AID targetAID = AgentRegistry.findOtherTownieAIDbyName(targetTownieName, myAgent );
        Integer targetRelationship = agentRelationships.getOrDefault(targetTownieName, 0);
        System.out.println("target r" + targetRelationship);
        if ((targetRelationship < 4 || (targetRelationship < 7 && Helpers.hysteria >= 50)) && !samePositionTownies.contains(targetAID) && targetAID != null ) { 
            System.out.println(targetAID);

            ACLMessage acl = new ACLMessage(ACLMessage.PROPOSE);
            
            for (AID townieID : samePositionTownies) {
                acl.addReceiver(townieID);
            }
            String accusationContent = "ACCUSATION:" + myAgent.getLocalName() + ":" + targetTownieName;
            acl.setContent(accusationContent);
            String conversationId = "accusation" ;
            acl.setConversationId(conversationId);
            myAgent.send(acl);
            instance.logToUI
            (myAgent.getAID().getLocalName() + ": " + targetTownieName + " is a witch!");
            
            agent.addBehaviour(new CollectAccusationSupportBehaviour(agent, targetAID, samePositionTownies));
        }
        else {
        	performGossip(samePositionTownies);
        }
        
    }
}