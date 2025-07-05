package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import behaviours.AccusationListeningBehavior;
import behaviours.GossipListeningBehavior;
import behaviours.RandomMoveBehaviour;
import behaviours.TownieActivitiesBehaviour;
import behaviours.UpdateRelationshipsBehaviour;
import helpers.AgentRegistry;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import onthology.OntologyExtractor;
import ui.GridManager;

public class TownieAgent extends RegistryAgent {
	private static final long serialVersionUID = -6918905687097287626L;
	public Map<String, Integer> relationships = new HashMap<>(); 
	private Map<String, String> onthologyArguments;
	private String profession;
	private String gender;
	private int socialStatus;
    public TownieAgent() {
    }

    public Map<String, Integer> getRelationships() {
        return relationships;
    }

    public void setRelationships(Map<String, Integer> relationships) {
        this.relationships = relationships;
    }

    @Override
    @SuppressWarnings("unchecked")
	protected void onSetup() {
        Object[] args = getArguments();
        if (args[0] instanceof Map) {
        this.onthologyArguments = (Map<String, String>) args[0];
        }
        System.out.println(onthologyArguments);
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription serviceDesc = new ServiceDescription();
        serviceDesc.setName("townie");
        serviceDesc.setType("townie");
        dfd.addServices(serviceDesc);
        setSocialStatus();
        setProfession();
        setGender();
        GridManager gridManager = GridManager.getInstance();
        gridManager.addAgent(getLocalName(), getGender());
        
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        
        Random random = new Random();
        addBehaviour(new GossipListeningBehavior(this));

        addBehaviour(new AccusationListeningBehavior());
        addBehaviour(new UpdateRelationshipsBehaviour());
        addBehaviour(new RandomMoveBehaviour(this, random.nextInt(10000, 20000)));
       
        initializeRelationships(AgentRegistry.findAgentsByTypeExcludingSelf(this),onthologyArguments);
      
//        System.out.println(relationships);

    }

    public void initializeRelationships(ArrayList<String> townies, Map<String, String> preferences) {
        Random random = new Random();
        int value;

        for (String townie : townies) {
     
        	String name = preferences.getOrDefault(townie, "noPreference");
        	switch (name) {
            case "likes":
                value = random.nextInt(6, 9);
                break;
            case "dislikes":
                value = random.nextInt(0, 3);
                break;
            case "relative_of":
                value = 10;
                break;
            default:
                value = random.nextInt(4, 6);
                break;
        }
            relationships.put(townie, value);
        }
    }
   
   
    public String getLeastLikedTownie() {
    		
    	   return relationships.entrySet()
    		        .stream()
    		        .min(Map.Entry.comparingByValue())
    		        .map(Map.Entry::getKey)
    		        .orElse(null);
    }
    

    
    public void removeFromRelationships(String agentName) {
       relationships.remove(agentName);
     
    }
    
    
 

	public int getSocialStatus() {
		return socialStatus;
	}

	private void setSocialStatus() {
		String socialStatus_raw = onthologyArguments.get("has_Social_Status");
    	switch (socialStatus_raw) {
        case "Rich":
            this.socialStatus = 2;
            break;
        case "Middle":
    	   this.socialStatus = 1;  
    	   break;
        case "Poor":
    	   this.socialStatus = 0;   
    	   break;
        case "Slave":
    	   this.socialStatus = -1;   
    	   break;
        default:
    	   this.socialStatus = -2;     
    	   break;
		}
	}

	public String getGender() {
		return gender;
	}

	private void setGender() {
		this.gender = onthologyArguments.get("Gender");
	
	}

	public String getProfession() {
		return profession;
	}

	private void setProfession() {
		this.profession = onthologyArguments.get("Profession");
	}

	@Override
	protected String getAgentType() {
		return "townie";
	}
} 
    