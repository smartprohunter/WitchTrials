package behaviours;

import java.util.Random;

import agents.TownieAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GossipListeningBehavior extends CyclicBehaviour {
    private static final long serialVersionUID = -8321411273717125553L;
	TownieAgent agent;
    Random random = new Random();
    
    public GossipListeningBehavior(TownieAgent a) {
        super(a);
        this.agent = a;
    }

    @Override
    public void action() {
    	
    	MessageTemplate mt = MessageTemplate.and(
              MessageTemplate.MatchConversationId("gossip"+ System.currentTimeMillis()%10000 + "_"),
              MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        ACLMessage acl = myAgent.receive(mt);

        if (acl != null) {
            String content = acl.getContent();
            double r = random.nextDouble(0, 1);
            String[] parts = content.split("::", 3);
            String targetName = parts[1];
            Integer currentRelationship = agent.getRelationships().getOrDefault(targetName, 0);

            if (r > 0.3 && currentRelationship != 10) { // da ne priema kluki za semeistvoto si
                handleMessage(content, targetName);
            } else{
                System.out.println(agent.getLocalName() + ": " + acl.getSender().getLocalName() + ", I don't trust you!");
            }
        }
    }
    
    public void handleMessage(String content, String targetName) {
                                     
            int r = random.nextInt(-4, -1);
            Integer currentValue = agent.getRelationships().getOrDefault(targetName, 0);
//            System.out.println(myAgent.getLocalName());

//            System.out.println(agent.getRelationships());
            
            agent.relationships.put(targetName, currentValue + r);
            
//            System.out.println(agent.getRelationships());

            System.out.println(agent.getLocalName() + ": Now that I think about it, " + targetName +" isn't that cool of a guy");
        
    }
}